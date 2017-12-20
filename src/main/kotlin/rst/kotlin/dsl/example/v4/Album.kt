package rst.kotlin.dsl.example.v4

import rst.kotlin.dsl.example.printPrettyJson
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

@DslMarker
annotation class  MusicDsl

data class Music(val albums: List<Album> = listOf())

data class Album(val title: String, val year: Int, val producer: String,
                 val chartUK: Int, val chartUS: Int,
                 val tracks: List<Track> = listOf())

data class Track(val title: String, val durationInSeconds: Int)

infix fun Int.min(sec: Int) = this * 60 + sec

fun music(init: MusicBuilder.() -> Unit): Music {
    val musicBuilder = MusicBuilder()
    musicBuilder.init()
    return musicBuilder.build()
}

object albertO {}

@MusicDsl
class MusicBuilder() {
    private val albums = mutableListOf<Album>()

    fun build(): Music = Music(albums)

    fun album(title: String, init: AlbumBuilder.() -> Unit) {
        val albumBuilder = AlbumBuilder(title)
        albumBuilder.init()
        albums.add(albumBuilder.build())
    }

    infix fun String.by(albumBuilder: AlbumBuilder): AlbumBuilder {
        albumBuilder.title = this
        return albumBuilder
    }

    operator fun String.invoke(init: AlbumBuilder.() -> Unit): AlbumBuilder {
        val albumBuilder = AlbumBuilder()
        albumBuilder.band = this
        albumBuilder.init()
        return albumBuilder
    }
}

@MusicDsl
class AlbumBuilder(internal var title: String = "", internal var band: String = "") {

    val recorded = Recorded()

    private var chartUk = 0
    private var chartUS = 0
    private val trackList = mutableListOf<TrackBuilder>()

    fun build(): Album = Album(title, recorded.year, recorded.producer, chartUk, chartUS, trackList.map { it.build() })

    fun charts(init: Charts.() -> Unit) {
        val builder = Charts()
        builder.init()
        chartUk = builder.inUK
        chartUS = builder.inUS
    }


    fun track(title: String, durationInSeconds: Int) {
        trackList.add(TrackBuilder(title, durationInSeconds))
    }

    operator fun String.unaryMinus(): TrackBuilder {
        val track = TrackBuilder(this)
        trackList.add(track)
        return track
    }

    val track: KProperty0<MutableList<TrackBuilder>>
        get() = this::trackList

    infix fun KProperty0<MutableList<TrackBuilder>>.title(value: String): TrackBuilder {
        val track = TrackBuilder(value)
        trackList.add(track)
        return track
    }
}

@MusicDsl
class TrackBuilder(private val title: String, private var durationInSeconds: Duration = 0) {

    internal fun build(): Track = Track(title, durationInSeconds)

    infix fun duration(seconds: Duration): KMutableProperty0<Duration> {
        durationInSeconds = seconds
        return this::durationInSeconds
    }

}
typealias Duration = Int

infix fun KMutableProperty0<Duration>.min(sec: Duration) {
    this.set(this.get() * 60 + sec)
}


@MusicDsl
class Recorded() {
    internal var year: Int = 0
    internal var producer: String = ""

    infix fun anno(value: Int): Recorded {
        year = value
        return this
    }

    infix fun by(value: String): Recorded {
        producer = value
        return this
    }
}


operator fun Int.contains(recorded: Recorded): Boolean {
    recorded.year = this
    return true
}

typealias ChartLevel = Int

@MusicDsl
class Charts() {
    internal var inUK: ChartLevel = 0
    internal var inUS: ChartLevel = 0

    val UK: KMutableProperty0<ChartLevel>
        get() = this::inUK

    val US: KMutableProperty0<ChartLevel>
        get() = this::inUS

    infix fun KMutableProperty0<ChartLevel>.on(value: ChartLevel): Charts {
        this.set(value)
        return this@Charts
    }

}

fun main(args: Array<String>) {

    val music =
            music() {
                album("The Dark Side of the Moon") {
                    recorded by "who knows" in 1973

                    charts {

                        UK on 6
                        UK on 3
                        US on 1
                    }
                    track("Speak to Me", 90)
                    track("Breathe", 2 min 43)

                    track title "Speak to me" duration 2 min 7
                }
                album("Licensed to Ill") {

                    recorded anno 1987 by "Rick Rubin"
                    charts {
                        UK on 23
                        US on 17
                    }
                    -"Fight for your right" duration 2 min 67
                    -"No sleep til Brooklyin" duration 3 min 11
                }

                "Reign in Blood" by "Slayer" {
                    recorded anno 1987 by "tjkjhg"
                    charts {
                        UK on 23
                        US on 17
                    }
                    -"Angel of Death" duration 2 min 23
                    -"Reign in Blood" duration 3 min 11
                }
            }

    printPrettyJson(music)

// TODO JSON pretty print
}


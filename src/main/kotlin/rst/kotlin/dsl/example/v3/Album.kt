package rst.kotlin.dsl.example.v3

import rst.kotlin.dsl.example.printPrettyJson


data class Music(val albums: MutableList<Album> = mutableListOf())

data class Album(val title: String, val year: Int, val producer: String,
                 val chartUK: Int, val chartUS: Int,
                 val tracks: List<Track> = listOf())

data class Track(val title: String, val durationInSeconds: Int)


fun music(init: MusicBuilder.() -> Unit): Music {
    val musicBuilder = MusicBuilder()
    musicBuilder.init()
    return musicBuilder.build()
}

class MusicBuilder() {
    private val albums = mutableListOf<Album>()

    fun build(): Music = Music(albums)

    fun album(title: String, init: AlbumBuilder.() -> Unit) {
        val albumBuilder = AlbumBuilder(title)
        albumBuilder.init()
        albums.add(albumBuilder.build())
    }
}

class AlbumBuilder(private val title: String) {
    private var year = 0
    private var producer = ""
    private var chartUk = 0
    private var chartUS = 0
    private val trackList = mutableListOf<Track>()

    fun build(): Album = Album(title, year, producer, chartUk, chartUS, trackList)

    fun charts(init: ChartsBuilder.() -> Unit) {
        val builder = ChartsBuilder()
        builder.init()
        chartUk = builder.uk
        chartUS = builder.us
    }

    fun track(title: String, durationInSeconds: Int) {
        trackList.add(Track(title, durationInSeconds))
    }

    fun recorded(init: RecordedBuilder.() -> Unit) {
        val builder = RecordedBuilder()
        builder.init()
        year = builder.inYear
        producer = builder.by
    }
}

data class RecordedBuilder(var inYear: Int = 0, var by: String = "")

data class ChartsBuilder(var uk: Int = 0, var us: Int = 0)


fun main(args: Array<String>) {

    val music =
            music() {
                album("The Dark Side of the Moon") {
                    recorded {
                        inYear = 1973
                        by = "who knows"
                    }
                    charts {
                        uk = 3
                        us = 1
                    }
                    track("Speak to Me", 90)
                    track("Breathe", 163)
                }
            }

    printPrettyJson(music)

}


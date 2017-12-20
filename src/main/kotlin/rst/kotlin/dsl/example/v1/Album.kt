package rst.kotlin.dsl.example.v1

data class Music(val albums:MutableList<Album> = mutableListOf())

data class Album(val title: String, var year: Int = 0, var producer: String = "",
                 var chartUK: Int = 0, var chartUS: Int = 0,
                 val tracks: MutableList<Track> = mutableListOf())

data class Track(val title: String, val durationInSeconds: Int)


fun music(init: Music.() -> Unit): Music {
    val music = Music()
    music.init()
    return music
}

fun Music.album(title: String, init: Album.() -> Unit) {
    val album = Album(title)
    album.init()
    albums.add(album)
}

fun Album.track(title:String, durationInSeconds: Int) {
    val track = Track(title,durationInSeconds)
    tracks.add(track)
}

fun Album.charts(uk: Int = 0, us: Int=0) {
    chartUK = uk
    chartUS = us
}

fun main(args: Array<String>) {

    val music =
            music() {
                album("The Dark Side of the Moon") {

                    year = 1973
                    producer = "who knows"
                    charts(uk = 3, us = 1)
                    track("Speak to Me", 90)
                    track("Breathe", 163)
                }
            }

    println(music)

    // TODO JSON pretty print
}


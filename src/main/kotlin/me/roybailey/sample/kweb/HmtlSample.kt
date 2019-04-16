import io.kweb.Kweb
import io.kweb.dom.element.new
import io.kweb.dom.element.creation.tags.h1
import io.kweb.dom.element.creation.tags.li
import io.kweb.dom.element.creation.tags.ul
import io.kweb.routing.route
import io.kweb.state.path
import io.kweb.state.simpleUrlParser

fun main() {
    Kweb(port = 8090) {
        doc.body.new {
            route {
                path("/") {
                    val path = url(simpleUrlParser).path
                    path.value = "/lists"
                }
                path("/lists") { params ->
                    ul().new {
                        for (x in 1..5) {
                            li().text("Hello World $x!")
                        }
                    }
                }
                path("/users/{userId}") { params ->
                    val userId = params.getValue("userId")
                    h1().text(userId.map { "User id: $it" })
                }
                path("/lists/{listId}") { params ->
                    val listId = params.getValue("listId")
                    h1().text(listId.map { "List id: $it" })
                }
            }
        }
    }
}


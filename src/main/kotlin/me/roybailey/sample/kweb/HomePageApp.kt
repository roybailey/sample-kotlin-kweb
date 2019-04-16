import io.kweb.*
import io.kweb.dom.BodyElement
import io.kweb.dom.element.Element
import io.kweb.dom.element.creation.ElementCreator
import io.kweb.dom.element.creation.tags.*
import io.kweb.dom.element.events.ONReceiver
import io.kweb.dom.element.events.on
import io.kweb.dom.element.new
import io.kweb.plugins.semanticUI.semantic
import io.kweb.plugins.semanticUI.semanticUIPlugin
import io.kweb.routing.route
import io.kweb.state.KVar
import io.kweb.state.path
import io.kweb.state.simpleUrlParser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch


/* ******************************
    SEMANTIC UI PACKAGED ELEMENTS
 */
open class SUISegment(parent: Element) : Element(parent)

fun ElementCreator<Element>.suiSegment(attributes: Map<String, Any> = semantic.ui.segment) = SUISegment(div(attributes))

open class SUIMenuItem(parent: Element) : Element(parent)

fun ElementCreator<Element>.suiMenuItem(href: String, text: String, attributes: Map<String, Any> = semantic.item) = SUIMenuItem(a(attributes, href).text(text))

open class SUITwoColumn(parent: Element) : Element(parent)

fun ElementCreator<Element>.suiTwoColumn(attributes: Map<String, Any> = semantic.ui.stackable.center.two.column.aligned.grid) = SUITwoColumn(div(attributes))

open class SUIVerticalDivider(parent: Element) : Element(parent)

fun ElementCreator<Element>.suiVerticalDivider(attributes: Map<String, Any> = semantic.ui.vertical.divider) = SUIVerticalDivider(div(attributes))

open class SUIRow(parent: Element) : Element(parent)

fun ElementCreator<Element>.suiRow(attributes: Map<String, Any> = semantic.ui.middle.aligned.row) = SUIRow(div(attributes))

open class SUIColumn(parent: Element) : Element(parent)

fun ElementCreator<Element>.suiColumn(attributes: Map<String, Any> = semantic.ui.column) = SUIColumn(div(attributes))

fun ElementCreator<Element>.suiTwoColumnDivider(
        divider: String,
        content1: ElementCreator<SUIRow>.() -> Unit) =
        suiSegment().new {
            suiTwoColumn().new {
                suiVerticalDivider().text(divider)
                suiRow().new {
                    content1(this)
                }
            }
        }

/* ****************************** */


fun main() {
    val plugins = listOf(semanticUIPlugin)
    Kweb(port = 8092, debug = true, plugins = plugins) {
        doc.body.new {
            // create menu...
            val menu = createMainMenu(linkedMapOf("One" to "/one", "Two" to "/two", "Three" to "/three"))
            // create routes to pages...
            route {
                val path = url(simpleUrlParser).path
                logger.info("router.url=${path.value}")
                menu[path.value]?.setAttribute("class", "semantic item active")

                path("/") {
                    path.value = "/one"
                }
                path("/one") { params ->
                    logger.info("path.params=${params}")
                    suiTwoColumnDivider(">>>") {

                        val text = KVar("results")
                        suiColumn().new {
                            val query = input(type = InputType.text, name = "query")
                            query.on.keypress { keypressEvent ->
                                handleQueryInput(query, keypressEvent, text)
                            }
                        }
                        suiColumn().text(text)
                    }
                }
            }
        }
    }
}


/**
 * Builds the main menu
 * @return a map of path to kweb elements, so the (active) class attribute can be manipulated
 */
private fun ElementCreator<BodyElement>.createMainMenu(menuItems: LinkedHashMap<String, String>) : Map<String, SUIMenuItem> {
    val menu = mutableMapOf<String, SUIMenuItem>()
    div(semantic.ui.pointing.menu).new {
        menuItems.forEach { title, href -> menu.put(href, suiMenuItem(href, title)) }
        div(semantic.right.menu).new {
            div(semantic.item).new {
                div(semantic.ui.transparent.icon.input).new {
                    val search = input(type = InputType.text, name = "search")
                    search.on.keypress { keypressEvent ->
                        if (keypressEvent.code == "Enter") {
                            handleSearchInput(search)
                        } else {
                            logger.info("keypress ignored : ${keypressEvent.code}")
                        }
                    }
                    i(semantic.search.icon)
                }
            }
        }
    }
    logger.info { menuItems }
    logger.info { menu }
    return menu
}


private fun handleSearchInput(input: InputElement) {
    GlobalScope.launch {
        val newSearch = input.getValue().await()
        logger.info("GLOBAL SEARCH : $newSearch")
    }
}


private fun handleQueryInput(input: InputElement, event: ONReceiver.KeyboardEvent, text: KVar<String>) {
    GlobalScope.launch {
        val newItemText = input.getValue().await()
        text.value = newItemText
    }
}

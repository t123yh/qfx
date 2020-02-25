package no.tornado.fxsample.login

import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.text.Font
import tornadofx.*

class SecureScreen : View("Secure Screen") {
    val loginController: LoginController by inject()

    private val model = object : ViewModel() {
        val nickname = bind { SimpleStringProperty() }
    }

    fun setNick(nick: String) {
        model.nickname.value = nick
    }

    override val root = borderpane {
        setPrefSize(800.0, 600.0)

        top {
            label(title) {
                font = Font.font(22.0)
            }
        }

        center {
            vbox(spacing = 15) {
                alignment = Pos.CENTER

                label(model.nickname.value + ", If you can see this, you are successfully logged in!")

                hbox {
                    alignment = Pos.CENTER

                    button("Logout") {
                        setOnAction {
                            loginController.logout()
                        }
                    }

                    button("Exit") {
                        setOnAction {
                            Platform.exit()
                        }
                    }
                }
            }
        }
    }
}
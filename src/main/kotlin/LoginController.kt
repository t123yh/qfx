package no.tornado.fxsample.login

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream
import javafx.scene.control.TextInputDialog
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.io.ByteArrayInputStream
import kotlinx.io.core.IoBuffer
import net.mamoe.mirai.Bot
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.network.WrongPasswordException
import net.mamoe.mirai.utils.BotConfiguration
import net.mamoe.mirai.utils.LoginSolver
import tornadofx.Controller
import tornadofx.runLater
import java.util.*
import javax.imageio.ImageIO


class LoginController : Controller() {
    val loginScreen: LoginScreen by inject()
    val secureScreen: SecureScreen by inject()

    fun init() {
        with(config) {
            if (containsKey(USERNAME) && containsKey(PASSWORD))
                string(USERNAME)?.let { string(PASSWORD)?.let { it1 -> tryLogin(it, it1, true) } }
            else
                showLoginScreen("Please log in")
        }
    }

    fun showLoginScreen(message: String, shake: Boolean = false) {
        secureScreen.replaceWith(loginScreen, sizeToScene = true, centerOnScreen = true)
        runLater {
            if (shake) loginScreen.shakeStage()
        }
    }

    fun showSecureScreen() {
        loginScreen.replaceWith(secureScreen, sizeToScene = true, centerOnScreen = true)
    }

    fun tryLogin(username: String, password: String, remember: Boolean) {
        val qqnum = username.toLong()

        var config = BotConfiguration()
        config.loginSolver = object: LoginSolver() {
            override suspend fun onSolvePicCaptcha(bot: Bot, data: IoBuffer): String? {
                val dialog = TextInputDialog("")

                val x = ByteArray(data.readRemaining)
                data.readAvailable(x, 0, x.size)
                dialog.graphic = ImageView(Image(ByteArrayInputStream(x)))
                dialog.title = "验证码"
                dialog.headerText = "请输入验证码，留空则换一张"

                val result: Optional<String> = dialog.showAndWait()
                return result.get()
            }

            override suspend fun onSolveSliderCaptcha(bot: Bot, url: String): String? {
                throw NotImplementedError("滑动验证码尚未实现，有大哥用 JavaFX WebView 写一下吗？")
            }

            override suspend fun onSolveUnsafeDeviceLoginVerify(bot: Bot, url: String): String? {
                throw NotImplementedError("设备锁尚未实现，有大哥用 JavaFX WebView 写一下吗？")
            }
        }

        GlobalScope.launch(Dispatchers.Main) { // launch coroutine in the main thread
            var bot = Bot(qqnum, password, config)
            try {
                bot.login()
                loginScreen.clear()
                showSecureScreen()
            }
            catch (e: WrongPasswordException) {
                e.message?.let { loginScreen.setMessage(it) }
                loginScreen.shakeStage()
            }
        }

    }

    fun logout() {
        with(config) {
            remove(USERNAME)
            remove(PASSWORD)
            save()
        }

        showLoginScreen("Log in as another user")
    }

    companion object {
        val USERNAME = "username"
        val PASSWORD = "password"
    }

}
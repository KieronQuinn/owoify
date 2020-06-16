package com.kieronquinn.app.owoify

import android.text.SpannableStringBuilder
import android.widget.TextView
import android.widget.TextView.BufferType
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import kotlin.random.Random

class OwO : IXposedHookZygoteInit {

    /*
        Prefixes, suffixes and replacements are directly copied from the excellent owo by zuzak (which was easier to convert than the original iOS tweak)
        https://github.com/zuzak/owo
     */

    companion object {

        val prefixes = arrayOf(
                "<3 ",
                "0w0 ",
                "H-hewwo?? ",
                "HIIII! ",
                "Haiiii! ",
                "Huohhhh. ",
                "OWO ",
                "OwO ",
                "UwU "
        )

        val suffixes = arrayOf(
                " :3",
                " UwU",
                " (✿ ♡‿♡)",
                " ÙωÙ",
                " ʕʘ‿ʘʔ",
                " ʕ•̫͡•ʔ",
                " >_>",
                " ^_^",
                "..",
                " Huoh.",
                " ^-^",
                " ;_;",
                " ;-;",
                " xD",
                " x3",
                " :D",
                " :P",
                " ;3",
                " XDDD",
                ", fwendo",
                " ㅇㅅㅇ",
                " (人◕ω◕)",
                "（＾ｖ＾）",
                " x3",
                " ._.",
                " (　\"◟ \")",
                " (• o •)",
                " (；ω；)",
                " (◠‿◠✿)",
                " >_<"
        )

        val substitutions = mapOf(
                Pair("r", "w"),
                Pair("l", "w"),
                Pair("R", "W"),
                Pair("L", "W"),
                Pair("no", "nu"),
                Pair("has", "haz"),
                Pair("have", "haz"),
                Pair("you", "uu"),
                Pair("the ", "da "),
                Pair("The ", "Da ")
        )
    }

    /*
        Hook code loosely based off Hodor by GermainZ
        https://github.com/GermainZ/Hodor
     */

    override fun initZygote(hodorHodorHodorHodor: StartupParam) {
        val hook: XC_MethodHook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                var text = param.args[0] as CharSequence
                val randomPrefix = prefixes.randomWeighted()
                val randomSuffix = suffixes.randomWeighted()
                substitutions.forEach {
                    text = text.replace(it.key.toRegex(), it.value)
                }
                val output = SpannableStringBuilder().apply {
                    if(randomPrefix != null){
                        append(randomPrefix)
                        append(" ")
                    }
                    append(text)
                    if(randomSuffix != null) {
                        append(" ")
                        append(randomSuffix)
                    }
                }
                param.args[0] = output as CharSequence
            }
        }
        XposedHelpers.findAndHookMethod(TextView::class.java, "setText", CharSequence::class.java, BufferType::class.java,
                Boolean::class.javaPrimitiveType, Int::class.javaPrimitiveType, hook)
        XposedHelpers.findAndHookMethod(TextView::class.java, "setHint", CharSequence::class.java, hook)
    }

    /*
        Adding a prefix and suffix to every single piece of text made the device impossible to navigate. Instead, we'll do it randomly with a chance of 1 in 3
     */
    private fun Array<String>.randomWeighted(): String? {
        val randomNumber = Random.nextInt(0, size * 3)
        return if(randomNumber > size) null
        else get(randomNumber)
    }
}
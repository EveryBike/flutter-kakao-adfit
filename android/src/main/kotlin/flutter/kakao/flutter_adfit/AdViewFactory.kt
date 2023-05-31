package flutter.kakao.flutter_adfit

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.kakao.adfit.ads.ba.BannerAdView
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.JSONMessageCodec
import io.flutter.plugin.common.PluginRegistry.Registrar
import io.flutter.plugin.platform.PlatformViewFactory

class AdViewFactory
    private constructor(private val messenger: BinaryMessenger?, private val appContext: Context?)
    : PlatformViewFactory(JSONMessageCodec.INSTANCE), ActivityLifecycleCallbacks {

    var activity: Activity? = null
    private var adView: NativeAdView? = null

    override fun create(context: Context, id: Int, args: Any?): NativeAdView {
        activity?.let {
            adView = NativeAdView(it, messenger, id, args)
            it.application.registerActivityLifecycleCallbacks(this)
        }
        return adView!!
    }

    fun onDestroy() {
        adView?.dispose()
    }

    companion object {
        /**
         * Flutter Android v1 API (using Registrar)
         */
        fun registerWith(registrar: Registrar): AdViewFactory {
            val plugin = AdViewFactory(registrar.messenger(), registrar.activity())
            registrar.platformViewRegistry().registerViewFactory("flutter.kakao.adfit/AdFitView", plugin)
            registrar.addViewDestroyListener {
                plugin.onDestroy()
                false
            }
            return plugin
        }

        /**
         * Flutter Android v2 API (using FlutterPluginBinding)
         */
        fun registerWith(flutterPluginBinding: FlutterPluginBinding): AdViewFactory {
            val plugin = AdViewFactory(flutterPluginBinding.binaryMessenger,
                    flutterPluginBinding.applicationContext)
            flutterPluginBinding.platformViewRegistry.registerViewFactory(
                    "flutter.kakao.adfit/AdFitView", plugin)
            return plugin
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        if (activity == this.activity) {
            (adView?.getView() as? BannerAdView)?.resume()
        }
    }

    override fun onActivityPaused(activity: Activity) {
        if (activity == this.activity) {
            (adView?.getView() as? BannerAdView)?.pause()
        }
    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (activity == this.activity) {
            activity.application.unregisterActivityLifecycleCallbacks(this)
            (adView?.getView() as? BannerAdView)?.destroy()
        }
    }

}

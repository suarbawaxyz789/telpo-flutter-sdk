package me.aljan.telpo_sdk_flutter

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** TelpoSdkFlutterPlugin */
class TelpoSdkFlutterPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private val TAG = "TelpoSdkFlutterPlugin"
    private lateinit var channel: MethodChannel
    private val channelId = "me.aljan.telpo_sdk_flutter/telpo"
    private var lowBattery = false
    private var _isConnected = false
    lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var binding: FlutterPlugin.FlutterPluginBinding
    private lateinit var telpoThermalPrinter: TelpoThermalPrinter

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, channelId)
        channel.setMethodCallHandler(this)
        binding = flutterPluginBinding
        context = flutterPluginBinding.applicationContext
        telpoThermalPrinter = TelpoThermalPrinter(this@TelpoSdkFlutterPlugin)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        val resultWrapper = MethodChannelResultWrapper(result)

        when (call.method) {
            "connect" -> {
                Log.e(TAG, _isConnected.toString())

                if (!_isConnected) {

                    val pIntentFilter = IntentFilter()
                    pIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
                    pIntentFilter.addAction("android.intent.action.BATTERY_CAPACITY_EVENT")

                    context.registerReceiver(printReceive, pIntentFilter)
                    val isConnected = telpoThermalPrinter.connect()
                    _isConnected = isConnected

                    resultWrapper.success(_isConnected)
                }
            }
            "checkStatus" -> {
                telpoThermalPrinter.checkStatus(resultWrapper, lowBattery)
            }
            "isConnected" -> {
                resultWrapper.success(_isConnected)
            }
            "disconnect" -> {
                if (_isConnected) {
                    Log.d(TAG, "disconnected")
                    context.unregisterReceiver(printReceive)
                    val disconnected = telpoThermalPrinter.disconnect()
                    _isConnected = false
                    resultWrapper.success(disconnected)
                }
            }
            "print" -> {
                val printDataList =
                    call.argument<ArrayList<Map<String, Any>>>("data") ?: ArrayList()

                telpoThermalPrinter.print(resultWrapper, printDataList, lowBattery)
            }
            else -> {
                resultWrapper.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        if (_isConnected) {
            context.unregisterReceiver(printReceive)
        }
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {
    }

    private val printReceive: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if (action == Intent.ACTION_BATTERY_CHANGED) {
                val status = intent.getIntExtra(
                    BatteryManager.EXTRA_STATUS,
                    BatteryManager.BATTERY_STATUS_NOT_CHARGING
                )
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0)

                if (status != BatteryManager.BATTERY_STATUS_CHARGING) {
                    lowBattery = level * 5 <= scale
                }
            }
            //
            else if (action == "android.intent.action.BATTERY_CAPACITY_EVENT") {
                val status: Int = intent.getIntExtra("action", 0)
                val level: Int = intent.getIntExtra("level", 0)

                lowBattery = if (status == 0) {
                    level < 1
                } else {
                    false
                }
            }
        }
    }
}
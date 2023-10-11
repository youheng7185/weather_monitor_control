package com.example.weathermonitorcontrol

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {

    private lateinit var ipAddressEditText: EditText
    private lateinit var upButton: Button
    private lateinit var downButton: Button
    private lateinit var selectButton: Button
    private lateinit var homeButton: Button
    private lateinit var displayTextView: TextView
    private lateinit var receivedDataTextView: TextView // New TextView for received data

    private val UDP_SERVER_PORT = 8888

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ipAddressEditText = findViewById(R.id.ipAddressEditText)
        upButton = findViewById(R.id.upButton)
        downButton = findViewById(R.id.downButton)
        selectButton = findViewById(R.id.selectButton)
        homeButton = findViewById(R.id.homeButton)
        displayTextView = findViewById(R.id.displayTextView)
        receivedDataTextView = findViewById(R.id.receivedDataTextView)

        upButton.setOnClickListener { sendUDP("up") }
        downButton.setOnClickListener { sendUDP("down") }
        selectButton.setOnClickListener { sendUDP("select") }
        homeButton.setOnClickListener { sendUDP("home") }

        // Start a thread to listen for incoming UDP data
        Thread {
            val receiveSocket = DatagramSocket(UDP_SERVER_PORT)
            val buffer = ByteArray(1024)
            val receivePacket = DatagramPacket(buffer, buffer.size)

            while (true) {
                receiveSocket.receive(receivePacket)
                val receivedData = String(buffer, 0, receivePacket.length, StandardCharsets.UTF_8)
                runOnUiThread {
                    receivedDataTextView.text = "Received Data: \n$receivedData"
                }
            }
        }.start()
    }

    private fun sendUDP(message: String) {
        val ipAddress = ipAddressEditText.text.toString()
        val port = 8888

        Thread {
            try {
                val socket = DatagramSocket()
                val ip = InetAddress.getByName(ipAddress)
                val sendData = message.toByteArray(StandardCharsets.UTF_8)
                val packet = DatagramPacket(sendData, sendData.size, ip, port)
                socket.send(packet)
                socket.close()
            } catch (e: Exception) {
                runOnUiThread {
                    displayTextView.text = "Error: ${e.message}"
                }
            }
        }.start()
    }
}

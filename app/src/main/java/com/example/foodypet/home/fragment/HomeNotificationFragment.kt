package com.example.foodypet.home.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodypet.R
import com.example.foodypet.home.adapter.NotificationAdapter
import com.example.foodypet.home.model.NotificationItem

class HomeNotificationFragment : Fragment(R.layout.fragment_home_notification) {

    private lateinit var notificationRv: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationRv = view.findViewById(R.id.notification_rv)

        val notificationList = listOf(
            NotificationItem("랑이의 급여 시각이에요.", "2026.03.30 13:00"),
            NotificationItem("랑이의 급여 시각이에요.", "2026.03.30 9:00"),
            NotificationItem("도란도란 단호박이 거의 다 소진되었어요.", "2026.01.30 9:00")
        )

        notificationAdapter = NotificationAdapter(notificationList)

        notificationRv.layoutManager = LinearLayoutManager(requireContext())
        notificationRv.adapter = notificationAdapter

        val backButton = view.findViewById<View>(R.id.meal_back_iv)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}
package com.mehdisekoba.potea.data.model.onboarding

import com.mehdisekoba.potea.R


data class IntroSlide(
    val id: Int,
    val description: String,
    val iconResourceId: Int,
)

private val emoji = String(Character.toChars(0x1f44b))

val dataLocal = listOf(
    IntroSlide(
        1, "Welcome to\n" +
                "Potea $emoji \n The best online store and herbal e-commerce\n" +
                "Country program for your needs!!", R.drawable.pic_1
    ),
    IntroSlide(
        2, "We provide high\n" +
                "quality plants just\n" +
                "for you", R.drawable.pic_2
    ),
    IntroSlide(
        3, "Your satisfaction is\n" +
                "our number one\n" +
                "priority", R.drawable.pic_3
    ),
    IntroSlide(
        4, "Let's Shop Your\n" +
                "Favorite Plants with\n" +
                "Potea Now!", R.drawable.pic_4
    ),

    )






package com.mehdisekoba.potea.data.network

import com.mehdisekoba.potea.data.model.SimpleResponse
import com.mehdisekoba.potea.data.model.cart.ResponseCartList
import com.mehdisekoba.potea.data.model.cart.ResponseChangeCount
import com.mehdisekoba.potea.data.model.detail.ResponseAddBookmark
import com.mehdisekoba.potea.data.model.detail.ResponseDetails
import com.mehdisekoba.potea.data.model.detail.ResponsePriceHistory
import com.mehdisekoba.potea.data.model.detail.ResponseShowComment
import com.mehdisekoba.potea.data.model.favourite.ResponseFavourite
import com.mehdisekoba.potea.data.model.home.ResponseAmazing
import com.mehdisekoba.potea.data.model.home.ResponseBanner
import com.mehdisekoba.potea.data.model.home.ResponseProducts
import com.mehdisekoba.potea.data.model.login.ResponseRegister
import com.mehdisekoba.potea.data.model.profile.BodySubmitAddress
import com.mehdisekoba.potea.data.model.profile.ResponseProfile
import com.mehdisekoba.potea.data.model.profile.ResponseShowAddress
import com.mehdisekoba.potea.data.model.profile.ResponseSubmitAddress
import com.mehdisekoba.potea.data.model.search.ResponseSearch
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiServices {
    @POST("auth/register.php")
    @FormUrlEncoded
    suspend fun postLogin(
        @Field("email") email: String,
    ): Response<ResponseRegister>

    @GET("home/banners.php")
    suspend fun getBannerList(): Response<ResponseBanner>

    @GET("home/amazing.php")
    suspend fun getAmazingList(): Response<ResponseAmazing>

    @GET("home/newest_products.php")
    suspend fun getNewProductList(): Response<ResponseProducts>

    @GET("home/Popular_products.php")
    suspend fun getPopularProductList(): Response<ResponseProducts>

    @GET("products/product.php")
    suspend fun getDetailsProduct(
        @Query("id") productId: Int,
    ): Response<ResponseDetails>

    @GET("products/price_history.php")
    suspend fun getHistoryPriceChart(
        @Query("product_id") productId: Int,
    ): Response<ResponsePriceHistory>

    @FormUrlEncoded
    @POST("products/add_bookmark.php")
    suspend fun postAddToBookMark(
        @Field("product_id") productId: Int,
    ): Response<ResponseAddBookmark>

    @GET("products/show_comments.php")
    suspend fun getShowComment(
        @Query("product_id") productId: Int,
    ): Response<ResponseShowComment>

    @FormUrlEncoded
    @POST("products/add_comment.php")
    suspend fun postAddNewComment(
        @Field("product_id") productId: Int,
        @Field("content") content: String,
        @Field("title") title: String,
        @Field("score") score: Int,
    ): Response<SimpleResponse>

    @FormUrlEncoded
    @POST("basket/add_to_cart.php")
    suspend fun postAddToCart(
        @Field("product_id") productId: Int,
    ): Response<SimpleResponse>


    @GET("basket/basket_list.php")
    suspend fun getCartList(): Response<ResponseCartList>

    @FormUrlEncoded
    @POST("basket/change_count.php")
    suspend fun postChangeCountItem(
        @Field("cart_item_id") itemId: Int,
        @Field("count") countItem: Int
    ): Response<ResponseChangeCount>

    @GET("profile/show_bookmarks.php")
    suspend fun getBookmarkList(): Response<ResponseFavourite>

    @FormUrlEncoded
    @POST("basket/remove_from_basket.php")
    suspend fun postRemoveItem(@Field("cart_item_id") itemId: Int): Response<SimpleResponse>

    @GET("profile/show_profile.php")
    suspend fun getInfoUser(): Response<ResponseProfile>

    @FormUrlEncoded
    @POST("profile/edit_profile.php")
    suspend fun postUpdateProfile(
        @Field("name") name: String, @Field("family") family: String, @Field("phone") phone: String
    ): Response<SimpleResponse>

    @POST("profile/add_new_address.php")
    suspend fun postSubmitAddress(@Body body: BodySubmitAddress): Response<ResponseSubmitAddress>

    @POST("profile/avatar.php")
    suspend fun postUploadAvatar(@Body body: RequestBody): Response<Unit>

    @GET("profile/show_addresses.php")
    suspend fun getShowAddress(): Response<ResponseShowAddress>

    @FormUrlEncoded
    @POST("search/search_result.php")
    suspend fun postSearch(
        @Field("search") searchKeyword: String,
    ): Response<ResponseSearch>
}

package uk.sky.pch32.moshitest

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class MoshiTest {

    private lateinit var jsonAdapter: JsonAdapter<MyResponse>

    @Before
    fun setUp() {
        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        jsonAdapter = moshi.adapter<MyResponse>(MyResponse::class.java)

    }

    private fun deserialize(json: String) = jsonAdapter.fromJson(json)!!

    @Test
    fun `Deserialize empty response not null`() {
        //given
        val json = "{}"

        // when
        val result = jsonAdapter.fromJson(json)

        // then
        assertNotNull(result)
    }

    @Test
    fun `Deserialize empty response has default items`() {
        //given
        val json = "{}"

        // when
        val result: MyResponse = deserialize(json)

        // then
        assertThat(result.items, contains(Item("none", 1, 23)))
    }

    @Test
    fun `Deserialize response with empty list has no item`() {
        //given
        val json = "{ \"items\": [] }"

        // when
        val result: MyResponse = deserialize(json)

        // then
        assertTrue { result.items.isEmpty() }
    }

    @Test
    fun `Deserialize response with single full item has whole item`() {
        //given
        val json = "{ \"items\": [ {\"name\": \"some\" , \"thereSAlwaysAComplicatedName\": 2, \"noDefault\": 22, \"hasDefault\": \"some value\"} ] }"

        // when
        val result: MyResponse = deserialize(json)

        // then
        assertThat(result.items, contains(Item("some", 2, 22, "some value")))
    }

    @Test
    fun `Deserialize response with single item missing default property has item with default`() {
        //given
        val json = "{ \"items\": [ {\"name\": \"some\" , \"thereSAlwaysAComplicatedName\": 2, \"noDefault\": 22} ] }"

        // when
        val result: MyResponse = deserialize(json)

        // then
        assertThat(result.items, contains(Item("some", 2, 22)))
    }

    @Test(expected = JsonDataException::class)
    fun `Deserialize response with single item missing non-default property fails `() {
        //given
        val json = "{ \"items\": [ {\"name\": \"some\" , \"thereSAlwaysAComplicatedName\": 2} ] }"

        // when
        deserialize(json)

        // then
    }

}
package br.pucpr.authserver.users

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class SortDirTest {
    @Test
    fun `getByName should return ASC if name is null`() {
        val result = SortDir.getByName(null)
        result shouldBe SortDir.ASC
    }

    @ParameterizedTest
    @ValueSource(strings=["asc", "ASC", "aSc"])
    fun `getByName should ignore case`(asc: String) {
        val result = SortDir.getByName(asc)
        result shouldBe SortDir.ASC
    }

    @ParameterizedTest
    @ValueSource(strings=["desc", "DESC", "Desc"])
    fun `getByName should ignore case for desc`(desc: String) {
        val result = SortDir.getByName(desc)
        result shouldBe SortDir.DESC
    }

    @Test
    fun `getByName returns null if the name is invalid`() {
        SortDir.getByName("invalid") shouldBe null
    }
}

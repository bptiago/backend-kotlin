package br.pucpr.authserver.studios

import br.pucpr.authserver.errors.BadRequestException
import br.pucpr.authserver.games.Game
import br.pucpr.authserver.games.requests.CreateStudioRequest
import br.pucpr.authserver.studios.requests.UpdateStudioRequest
import br.pucpr.authserver.studios.responses.CreateStudioResponse
import br.pucpr.authserver.utils.SortDir
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import java.time.LocalDate

class StudioControllerTest {
    // Test data
    private companion object {
        const val TEST_ID = 1L
        const val TEST_NAME = "Test Studio"
        const val TEST_LOCATION = "Test Location"
        val TEST_DATE = LocalDate.of(2024, 1, 1)
    }

    // System under test
    private lateinit var studioController: StudioController

    // Mocks
    private val studioService = mockk<StudioService>()

    @BeforeEach
    fun setup() {
        studioController = StudioController(studioService)
    }

    @AfterEach
    fun cleanup() {
        clearAllMocks()
    }

    private fun createTestGame(id: Long = 1L) = Game(
        id = id,
        name = "Test Game $id",
        overview = "Test Overview",
        launchDate = LocalDate.now()
    )

    private fun createTestStudio(
        id: Long? = null,
        name: String = TEST_NAME,
        location: String = TEST_LOCATION,
        creationDate: LocalDate = TEST_DATE,
        games: MutableList<Game> = mutableListOf(createTestGame())
    ) = Studio(
        id = id,
        name = name,
        location = location,
        creationDate = creationDate,
        games = games
    )

    @Test
    fun `insert should return created status with studio response`() {
        val createRequest = CreateStudioRequest(
            name = TEST_NAME,
            location = TEST_LOCATION,
            creationDate = TEST_DATE
        )
        val studio = createTestStudio(TEST_ID)

        every { studioService.insert(any()) } returns studio

        val response = studioController.insert(createRequest)

        response.statusCode shouldBe HttpStatus.CREATED
        response.body shouldBe CreateStudioResponse(studio)
        verify { studioService.insert(any()) }
    }

    @Test
    fun `list should return ok status with studios when sorting ASC`() {
        val studios = listOf(
            createTestStudio(1L, "Studio A"),
            createTestStudio(2L, "Studio B")
        )
        every { studioService.list(SortDir.ASC) } returns studios

        val response = studioController.list("ASC")

        response.statusCode shouldBe HttpStatus.OK
        response.body shouldBe studios
        verify { studioService.list(SortDir.ASC) }
    }

    @Test
    fun `list should return ok status with studios when sorting DESC`() {
        val studios = listOf(
            createTestStudio(2L, "Studio B"),
            createTestStudio(1L, "Studio A")
        )
        every { studioService.list(SortDir.DESC) } returns studios

        val response = studioController.list("DESC")

        response.statusCode shouldBe HttpStatus.OK
        response.body shouldBe studios
        verify { studioService.list(SortDir.DESC) }
    }

    @Test
    fun `list should throw BadRequestException for invalid sort direction`() {
        assertThrows<BadRequestException> {
            studioController.list("INVALID")
        }.message shouldBe "Parâmetro SORT inválido (deve ser ASC ou DESC)"
    }

    @Test
    fun `findById should return ok status with studio`() {
        val studio = createTestStudio(TEST_ID)
        every { studioService.getStudio(TEST_ID) } returns studio

        val response = studioController.findById(TEST_ID)

        response.statusCode shouldBe HttpStatus.OK
        response.body shouldBe studio
        verify { studioService.getStudio(TEST_ID) }
    }

    @Test
    fun `update should return ok status with updated studio`() {
        val studio = createTestStudio(TEST_ID)
        val updateRequest = UpdateStudioRequest(
            name = "Updated Name",
            location = "Updated Location",
            creationDate = LocalDate.now()
        )

        every { studioService.update(TEST_ID, updateRequest) } returns studio

        val response = studioController.update(TEST_ID, updateRequest)

        response.statusCode shouldBe HttpStatus.OK
        response.body shouldBe studio
        verify { studioService.update(TEST_ID, updateRequest) }
    }

    @Test
    fun `delete should return ok status when successful`() {
        every { studioService.delete(TEST_ID) } returns Unit

        val response = studioController.delete(TEST_ID)

        response.statusCode shouldBe HttpStatus.OK
        verify { studioService.delete(TEST_ID) }
    }
}
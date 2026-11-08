package com.galindo.rickandmorty.ui.lista

import com.galindo.rickandmorty.data.auth.AuthRepository
import com.galindo.rickandmorty.data.repository.ItemRepository
import com.galindo.rickandmorty.domain.model.Item
import com.galindo.rickandmorty.ui.UiState
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ListaViewModelTest {

    private val itemRepository: ItemRepository = mockk(relaxed = true)
    private val authRepository: AuthRepository = mockk(relaxed = true)

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun obtenerItems_exito_actualizaUiStateAExito() = runTest(testDispatcher) {
        val mockItems = listOf(
            Item(1, "Rick Sanchez", "Human - Alive", "https://rickandmortyapi.com/api/character/avatar/1.jpeg", false)
        )
        every { itemRepository.items } returns flowOf(mockItems)
        coEvery { itemRepository.refrescar() } returns Result.success(1)

        val viewModel = ListaViewModel(itemRepository, authRepository)

        val state = viewModel.uiState.value
        assert(state is UiState.Exito) { "Expected UiState.Exito but got $state" }
        assertEquals(mockItems, (state as UiState.Exito).datos)
    }

    @Test
    fun obtenerItems_errorRed_conDatosLocales_muestraAvisoSinConexion() = runTest(testDispatcher) {
        val mockItems = listOf(
            Item(1, "Rick Sanchez", "Human - Alive", "https://rickandmortyapi.com/api/character/avatar/1.jpeg", false)
        )
        every { itemRepository.items } returns flowOf(mockItems)
        coEvery { itemRepository.refrescar() } returns Result.failure(IOException("No internet"))
        coEvery { itemRepository.cantidadGuardada() } returns 5

        val viewModel = ListaViewModel(itemRepository, authRepository)

        assertEquals("Sin conexión: mostrando datos guardados", viewModel.aviso.value)
    }

    @Test
    fun alternarFavorito_llamaAlRepositorioCorrectamente() = runTest(testDispatcher) {
        val characterId = 1
        every { itemRepository.items } returns flowOf(emptyList())
        coEvery { itemRepository.refrescar() } returns Result.success(0)
        coEvery { itemRepository.alternarFavorito(characterId) } just Runs

        val viewModel = ListaViewModel(itemRepository, authRepository)

        viewModel.alternarFavorito(characterId)

        coVerify { itemRepository.alternarFavorito(characterId) }
    }
}

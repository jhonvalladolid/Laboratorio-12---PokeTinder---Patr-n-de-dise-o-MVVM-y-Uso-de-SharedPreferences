package com.valladolid.jhon.poketinder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel: ViewModel() {
    val pokemonList = MutableLiveData<List<PokemonResponse>>()
    val isLoading = MutableLiveData<Boolean>()
    val errorApi = MutableLiveData<String>()

    init {
        getAllPokemons()
    }

    fun getAllPokemons() {
        isLoading.postValue(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call = getRetrofit().create(PokemonApi::class.java).getPokemons()
                if(call.isSuccessful) {
                    call.body()?.let {
                        isLoading.postValue(false)
                        pokemonList.postValue(it.results)
                    }
                } else {
                    isLoading.postValue(false)
                    errorApi.postValue("Error al obtener los Pokémon")
                }
            } catch (e: Exception) {
                isLoading.postValue(false)
                errorApi.postValue(e.message)
            }
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://pokeapi.co")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

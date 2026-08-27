package mx.com.getic.encuestasoxxo.data.repository

import mx.com.getic.encuestasoxxo.data.local.dao.CatalogoDao
import mx.com.getic.encuestasoxxo.data.local.entities.*
import mx.com.getic.encuestasoxxo.data.remote.ApiService
import mx.com.getic.encuestasoxxo.data.remote.dto.*

class CatalogoRepository(
    private val api: ApiService,
    private val catalogoDao: CatalogoDao
) {
    suspend fun obtenerNegocios(token: String, refrescar: Boolean = false): List<NegocioDto> {
        if (!refrescar) {
            val cache = catalogoDao.obtenerNegocios()
            if (cache.isNotEmpty()) return cache.map { NegocioDto(it.id, it.nombre, it.esDefault) }
        }
        val remote = api.negocios("Bearer $token")
        catalogoDao.guardarNegocios(remote.map { NegocioEntity(it.id, it.nombre, it.es_default) })
        return remote
    }

    suspend fun obtenerRegiones(token: String, negocioId: Int, refrescar: Boolean = false): List<RegionDto> {
        if (!refrescar) {
            val cache = catalogoDao.obtenerRegiones(negocioId)
            if (cache.isNotEmpty()) return cache.map { RegionDto(it.id, it.nombre, it.cr, it.esDefault) }
        }
        val remote = api.regiones("Bearer $token", negocioId)
        catalogoDao.guardarRegiones(remote.map { RegionEntity(it.id, negocioId, it.nombre, it.cr, it.es_default) })
        return remote
    }

    suspend fun obtenerPlazas(token: String, regionId: Int, refrescar: Boolean = false): List<PlazaDto> {
        if (!refrescar) {
            val cache = catalogoDao.obtenerPlazas(regionId)
            if (cache.isNotEmpty()) return cache.map { PlazaDto(it.id, it.nombre, it.cr, it.esDefault) }
        }
        val remote = api.plazas("Bearer $token", regionId)
        catalogoDao.guardarPlazas(remote.map { PlazaEntity(it.id, regionId, it.nombre, it.cr, it.es_default) })
        return remote
    }
    
    suspend fun obtenerRoles(token: String, refrescar: Boolean = false): List<RolDto> {
        if (!refrescar) {
            val cache = catalogoDao.obtenerRoles()
            if (cache.isNotEmpty()) return cache.map { RolDto(it.id, it.nombre) }
        }
        val remote = api.obtenerRoles("Bearer $token")
        catalogoDao.guardarRoles(remote.map { RolEntity(it.id, it.nombre) })
        return remote
    }
}

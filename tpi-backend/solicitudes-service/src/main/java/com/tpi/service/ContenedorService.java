package com.tpi.service;

import com.tpi.dto.request.ContenedorRequestDTO;
import com.tpi.dto.response.ContenedorResponseDTO;
import com.tpi.dto.response.EstadoContenedorInfoDTO;
import com.tpi.exception.ContenedorNoDisponibleException;
import com.tpi.exception.EntidadDuplicadaException;
import com.tpi.exception.EntidadNotFoundException;
import com.tpi.model.Contenedor;
import com.tpi.model.EstadoContenedor;
import com.tpi.repository.ContenedorRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ContenedorService {

    private final ContenedorRepository contenedorRepository;
    private final EstadoContenedorService estadoContenedorService;


    /**
     * Obtiene todos los contenedores, filtrando opcionalmente por estado.
     *
     * @param estado Estado por el cual filtrar los contenedores (si es null o vacío,
     *               se devuelven todos).
     * @return Lista de contenedores convertidos a DTO.
     */
    public List<ContenedorResponseDTO> findAll(String estado) {

        List<Contenedor> contenedores;

        if (estado != null && !estado.trim().isEmpty()) {
            // Si se envía un estado → buscar solo los contenedores con ese estado.
            // Esta consulta ya incluye el JOIN con Estado, por lo tanto es una sola query.
            contenedores = contenedorRepository.findByEstadoNombreWithEstado(estado);
        } else {
            // Si no se envía un estado → traer todos los contenedores.
            // También usa un JOIN para evitar consultas adicionales (N+1).
            contenedores = contenedorRepository.findAllWithEstado();
        }

        // Convertir cada entidad a su DTO correspondiente.
        return contenedores.stream()
            .map(ContenedorResponseDTO::fromEntity) // Evita queries adicionales al acceder a Estado
            .collect(Collectors.toList());
    }


    /**
     * Obtiene un contenedor por su ID.
     *
     * @param id ID del contenedor a buscar.
     * @return ContenedorResponseDTO con los datos del contenedor encontrado.
     *
     * @throws EntidadNotFoundException si el contenedor no existe,
     *         lo que se traduce en un HTTP 404.
     */
    public ContenedorResponseDTO getDTOById(Long id) {
        Contenedor contenedor = this.findById(id);
        return ContenedorResponseDTO.fromEntity(contenedor);
    }

    /**
     * Obtiene un contenedor por su ID.
     *
     * @param id ID del contenedor a buscar.
     * @return ContenedorResponseDTO con los datos del contenedor encontrado.
     *
     * @throws EntidadNotFoundException si el contenedor no existe,
     *         lo que se traduce en un HTTP 404.
     */
    public Contenedor findById(Long id) {
        return contenedorRepository.findByIdWithEstado(id)
            .orElseThrow(() -> new EntidadNotFoundException(
                "Contenedor no encontrado con ID: ", 
                id
            ));
    }

    /**
     * Valida que el contenedor esté disponible para operar.
     * 
     * @param contenedor Contenedor a validar.
     * @throws ContenedorNoDisponibleException si el contenedor no está en estado "DISPONIBLE".
     */
    public void validarDisponibilidad(Contenedor contenedor) {

        // Obtener el nombre del estado actual del contenedor
        String estado = contenedor.getEstado().getNombre();

        // Comparación correcta con equals(), no con "!="
        if (!"DISPONIBLE".equals(estado)) {
            throw new ContenedorNoDisponibleException(
                "El contenedor no está disponible. Estado actual: " + estado
            );
        }
    }

    /**
     * Actualiza el estado de un contenedor asignándole un nuevo estado válido.
     *
     * @param contenedor   Contenedor cuyo estado será actualizado.
     * @param nombreEstado Nombre del nuevo estado a aplicar (ej: "ASIGNADO", "DISPONIBLE").
     * @throws EntidadNotFoundException si el estado solicitado no existe en la tabla EstadoContenedor.
     */
    public void actualizarEstado(Contenedor contenedor, String nombreEstado) {
        EstadoContenedor estado = estadoContenedorService.findByNombre(nombreEstado);
        contenedor.setEstado(estado);
        this.save(contenedor);
    }


    /**
     * Actualiza el estado de un contenedor según su ID.
     *
     * @param id ID del contenedor que se desea actualizar.
     * @param nombreEstado Nombre del nuevo estado que se asignará al contenedor.
     *
     * @return ContenedorResponseDTO con los datos del contenedor actualizado
     *         y su nuevo estado.
     *
     * @throws EntidadNotFoundException si el contenedor con el ID dado no existe,
     *         lo que se traduce en un HTTP 404.
     */
    @SuppressWarnings("null")
    public ContenedorResponseDTO actualizarEstado(Long id, String nombreEstado) {
        Contenedor contenedor = contenedorRepository.findById(id)
            .orElseThrow(() -> new EntidadNotFoundException(
                "Contenedor no encontrado con ID: ", 
                id
            ));
        
        EstadoContenedor estado = estadoContenedorService.findByNombre(nombreEstado);
        
        contenedor.setEstado(estado);
        Contenedor updated = contenedorRepository.save(contenedor);
        
        // Crea el DTO del estado explícitamente
        EstadoContenedorInfoDTO estadoDTO = new EstadoContenedorInfoDTO(
            estado.getId(),
            estado.getNombre()
        );
        
        return ContenedorResponseDTO.fromEntity(updated, estadoDTO);
    }


    /**
     * Busca un contenedor por su identificación única.
     *
     * @param identificacionUnica Valor único que identifica al contenedor.
     * @return Optional<Contenedor> que contiene el contenedor si existe,
     *         o vacío si no fue encontrado.
     */
    public Optional<Contenedor> findByIdentificacionUnica(String identificacionUnica) {
        return contenedorRepository.findByIdentificacionUnica(identificacionUnica);
    }

    /**
     * Verifica si existe un contenedor con una identificación única dada.
     *
     * @param identificacionUnica Valor único que identifica al contenedor.
     * @return true si existe un contenedor con esa identificación,
     *         false en caso contrario.
     */
    public boolean existsByIdentificacionUnica(String identificacionUnica) {
        return contenedorRepository.existsByIdentificacionUnica(identificacionUnica);
    }


    /**
     * Crea un nuevo contenedor y devuelve su representación en DTO.
     * Valida que no exista un contenedor con la misma identificación única antes de crearlo.
     * Asigna automáticamente el estado "DISPONIBLE" al contenedor.
     *
     * @param requestDTO DTO con los datos necesarios para crear el contenedor.
     * @return ContenedorResponseDTO con los datos del contenedor creado y su estado.
     *
     * @throws EntidadDuplicadaException si ya existe un contenedor con la misma identificación única.
     */
    public ContenedorResponseDTO crearContenedor(ContenedorRequestDTO requestDTO) {
        // Validar que el estado exista sino propaga 404
        EstadoContenedor estado = estadoContenedorService.findByNombre("DISPONIBLE");

        // Validar duplicado usando excepción
        if (existsByIdentificacionUnica(requestDTO.identificacionUnica())) {
            throw new EntidadDuplicadaException(
                "Contenedor", 
                "identificación única", 
                requestDTO.identificacionUnica()
            );
        }
        
        // Crear el contenedor con los datos del request
        Contenedor contenedor = new Contenedor();
        contenedor.setPeso(requestDTO.peso());
        contenedor.setVolumen(requestDTO.volumen());
        contenedor.setIdentificacionUnica(requestDTO.identificacionUnica());
        contenedor.setEstado(estado);
        
        // Guardar en la base de datos
        Contenedor contenedorGuardado = contenedorRepository.save(contenedor);
        
        // Crear DTO del estado
        EstadoContenedorInfoDTO estadoDTO = new EstadoContenedorInfoDTO(
            estado.getId(),
            estado.getNombre()
        );
        
        // Devolver DTO completo
        return ContenedorResponseDTO.fromEntity(contenedorGuardado, estadoDTO);
    }


    /**
     * Obtiene un contenedor por su identificación única o lo crea si no existe.
     * Valida que el contenedor esté disponible y lo asigna al cliente indicado.
     *
     * @param requestDTO DTO con los datos necesarios para buscar o crear el contenedor.
     * @param keycloakId ID del cliente al cual se asignará el contenedor.
     * @return Contenedor con los datos actualizados y asignado al cliente.
     *
     * @throws ContenedorNoDisponibleException si el contenedor existe pero no está disponible.
     */
    public Contenedor crearContenedorAdminSolicitud(ContenedorRequestDTO requestDTO, String keycloakId) {
        EstadoContenedor estadoDisponible = estadoContenedorService.findByNombre("ASIGNADO");

        // Buscar o crear contenedor
        Contenedor contenedor = findByIdentificacionUnica(requestDTO.identificacionUnica())
            .orElseGet(() -> crearContenedorDisponible(requestDTO, estadoDisponible));

        // Validar disponibilidad
        if (!contenedor.getEstado().getNombre().equals(estadoDisponible.getNombre())) {
            throw new ContenedorNoDisponibleException("El contenedor no está disponible");
        }

        // Asignar al cliente
        return asignarACliente(contenedor, keycloakId);
    }


    /**
     * Crea un contenedor nuevo con estado "DISPONIBLE" y sin cliente asignado.
     *
     * @param requestDTO DTO con los datos del contenedor a crear.
     * @param estado Estado a asignar al contenedor (normalmente "DISPONIBLE").
     * @return Contenedor guardado en la base de datos.
     */
    private Contenedor crearContenedorDisponible(ContenedorRequestDTO requestDTO, EstadoContenedor estado) {
        Contenedor contenedor = new Contenedor();
        contenedor.setIdentificacionUnica(requestDTO.identificacionUnica());
        contenedor.setPeso(requestDTO.peso());
        contenedor.setVolumen(requestDTO.volumen());
        contenedor.setEstado(estado);
        contenedor.setClienteId(null); // Sin dueño inicialmente

        return this.save(contenedor);
    }


    /**
     * Asigna un contenedor a un cliente si aún no está asignado o si pertenece a otro cliente.
     *
     * @param contenedor Contenedor a asignar.
     * @param keycloakId ID del cliente a asignar.
     * @return Contenedor actualizado con el cliente asignado.
     */
    private Contenedor asignarACliente(Contenedor contenedor, String keycloakId) {
        if (contenedor.getClienteId() == null || !contenedor.getClienteId().equals(keycloakId)) {
            contenedor.setClienteId(keycloakId);
            return this.save(contenedor);
        }
        return contenedor;
    }

    /**
     * Guarda un contenedor en la base de datos.
     *
     * @param contenedor Contenedor a guardar.
     * @return Contenedor guardado.
     */
    @SuppressWarnings("null")
    public Contenedor save(Contenedor contenedor) {
        return contenedorRepository.save(contenedor);
    }

    /**
     * Elimina un contenedor por su ID.
     *
     * @param id ID del contenedor a eliminar.
     */
    @SuppressWarnings("null")
    public void deleteById(Long id) {
        contenedorRepository.deleteById(id);
    }
}
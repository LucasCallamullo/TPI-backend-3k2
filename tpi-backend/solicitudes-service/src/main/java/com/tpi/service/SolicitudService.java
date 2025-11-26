package com.tpi.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

import com.tpi.client.ClientesServiceClient;
import com.tpi.client.LogisticaServiceClient;
import com.tpi.dto.external.CostoFinalDTO;
import com.tpi.dto.external.CostosEstimadosDTO;
import com.tpi.dto.external.RutaResponses.RutaAsignadaResponseDTO;
import com.tpi.dto.external.RutaResponses.RutaTramosCamionResponse;
import com.tpi.dto.external.UbicacionResponses.UbicacionResponseDTO;
import com.tpi.dto.request.AsignarRutaRequest;
import com.tpi.dto.request.CrearRutaCompletaRequest;
import com.tpi.dto.request.SolicitudesRequestDTOs.SolicitudClienteRequestDTO;
import com.tpi.dto.request.SolicitudesRequestDTOs.SolicitudCompletaRequestDTO;

import com.tpi.dto.response.ContenedorResponseDTO;
import com.tpi.dto.response.SolicitudResponses.ClienteDTO;
import com.tpi.dto.response.SolicitudResponses.SolicitudResponseDTO;
import com.tpi.dto.response.SolicitudResponses.SolicitudWithRutaResponseDTO;
import com.tpi.dto.response.SolicitudResponses.SolicitudWithUbicacionAndRutaResponseDTO;
import com.tpi.dto.response.SolicitudResponses.SolicitudWithUbicacionResponseDTO;
import com.tpi.exception.AccessoDenegadoException;
import com.tpi.exception.EntidadNotFoundException;

import com.tpi.model.Solicitud;
import com.tpi.model.EstadoSolicitud;
import com.tpi.model.Contenedor;
import com.tpi.repository.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@Transactional
@RequiredArgsConstructor    // crea builder con todos los campos marcados en final, evitar builder verboso
public class SolicitudService {
    
    private final SolicitudRepository solicitudRepository;
    private final ContenedorService contenedorService;
    private final EstadoSolicitudService estadoSolicitudService;

    private final LogisticaServiceClient logisticaServiceClient;
    private final ClientesServiceClient clientesServiceClient;
    // private final SecurityContextService securityContextService;
    
    /**
     * Metodo para crear solicitud con solo contenedor en la peticion
     */
    @SuppressWarnings("null")
    public SolicitudWithUbicacionResponseDTO crearSolicitudCompleta(
        SolicitudCompletaRequestDTO request, String keycloakId) {
        
        // 2. Crear ubicaciones a través del ms logistica y preparar respuesta
        UbicacionResponseDTO origen = logisticaServiceClient.crearUbicacion(request.origen());
        UbicacionResponseDTO destino = logisticaServiceClient.crearUbicacion(request.destino());
        
        // 3. Obtener Cliente 
        // ClienteDTO cliente = clientesServiceClient.obtenerClientePorId(keycloakId);
        ClienteDTO cliente = clientesServiceClient.crearCliente(request.cliente());

        // 3. Crear contenedor en caso de ser necesario
        Contenedor contenedor = contenedorService.getOrCreate(request.contenedor(), keycloakId);
        
        // 4. Obtener estado
        EstadoSolicitud estadoBorrador = estadoSolicitudService.findByNombre("BORRADOR");
        
        // 5. Crear solicitud (SOLO lógica de dominio)
        Solicitud solicitud = Solicitud.builder()
            .clienteId(cliente.id())    // Keycloack Id del cliente creado utilizado como id en base de datos
            .contenedor(contenedor)
            .origenId(origen.id())
            .destinoId(destino.id())
            .estado(estadoBorrador)
            .costoEstimado(0.0)    // seteados en default 0.0 otro endpoint se encarga
            .tiempoEstimadoHoras(0.0)
            .distanciaTotalKM(0.0)
            .build();
            
        Solicitud saved = solicitudRepository.save(solicitud);
        
        return SolicitudWithUbicacionResponseDTO.fromEntity(
            saved, estadoBorrador, origen, destino, cliente
        );
    }


    /**
     * Metodo para crear solicitud desde  el cliente
     */
    @SuppressWarnings("null")
    public SolicitudWithUbicacionResponseDTO crearSolicitudCliente(
        SolicitudClienteRequestDTO request, String keycloakId) {
        
        // 2. Crear ubicaciones a través del ms logistica y preparar respuesta
        UbicacionResponseDTO origen = logisticaServiceClient.crearUbicacion(request.origen());
        UbicacionResponseDTO destino = logisticaServiceClient.crearUbicacion(request.destino());
        
        // 3. Obtener Cliente 
        ClienteDTO cliente = clientesServiceClient.obtenerClientePorId(keycloakId);

        // 3. Crear contenedor en caso de ser necesario
        Contenedor contenedor = contenedorService.findById(request.contenedorId());
        
        // 4. Obtener estado
        EstadoSolicitud estadoBorrador = estadoSolicitudService.findByNombre("BORRADOR");
        
        // 5. Crear solicitud (SOLO lógica de dominio)
        Solicitud solicitud = Solicitud.builder()
            .clienteId(keycloakId)    // Keycloack Id utilizado como id en base de datos
            .contenedor(contenedor)
            .origenId(origen.id())
            .destinoId(destino.id())
            .estado(estadoBorrador)
            .costoEstimado(0.0)    // seteados en default 0.0 otro endpoint se encarga
            .tiempoEstimadoHoras(0.0)
            .distanciaTotalKM(0.0)
            .build();
            
        Solicitud saved = solicitudRepository.save(solicitud);
        
        return SolicitudWithUbicacionResponseDTO.fromEntity(
            saved, estadoBorrador, origen, destino, cliente
        );
    }

    /**
     * Actualiza únicamente el estado de una solicitud existente.
     * 
     * @param id ID de la solicitud a actualizar
     * @param nuevoEstado Nombre del nuevo estado a asignar (ej: "PROGRAMADA")
     * @return DTO con la información actualizada de la solicitud
     */
    @SuppressWarnings("null")
    public SolicitudResponseDTO actualizarEstado(Long id, String nuevoEstado) {

        // 1. Buscar la solicitud por ID; si no existe, lanza excepción 404
        Solicitud solicitud = solicitudRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, 
                "Solicitud no encontrada con ID: " + id
            ));

        // 2. Obtener el objeto EstadoSolicitud correspondiente al nuevo estado
        EstadoSolicitud estado = estadoSolicitudService.findByNombre(nuevoEstado);

        // 3. Obtener información del cliente desde el microservicio clientes
        ClienteDTO cliente = clientesServiceClient.obtenerClientePorId(solicitud.getClienteId());

        // 4. Asignar el nuevo estado a la solicitud
        solicitud.setEstado(estado);

        // 5. Guardar la solicitud actualizada en la base de datos
        Solicitud updated = solicitudRepository.save(solicitud);

        // 6. Construir y retornar el DTO de respuesta con la solicitud, estado y cliente
        return SolicitudResponseDTO.fromEntity(updated, estado, cliente);
    }

    
    
    /**
     * Asigna una ruta a una solicitud existente, creando la ruta en MS-Logística
     * y actualizando la solicitud con la información resultante.
     *
     * @param id      ID de la solicitud.
     * @param request Datos necesarios para asignar la ruta.
     * @return Un DTO con la solicitud y la ruta asignada.
     * @throws EntidadNotFoundException si la solicitud no existe.
     * @throws RuntimeException si ocurre un error durante el proceso.
     */
    public SolicitudWithRutaResponseDTO asignarRuta(Long id, AsignarRutaRequest request) {
        log.info("=== INICIANDO crearRutaParaSolicitud ===");
 
        // 1. Obtener solicitud existente
        Solicitud solicitud = this.findById(id);

        // 2. Obtener cliente
        ClienteDTO cliente = clientesServiceClient.obtenerClientePorId(solicitud.getClienteId());
        
        // 2. crear request a ruta
        CrearRutaCompletaRequest rutaRequest = new CrearRutaCompletaRequest(
            solicitud.getId(),                 // solicitudId
            solicitud.getContenedor().getVolumen(),  // volumen de contenedor necesario para definir tarifa
            solicitud.getOrigenId(),            // referencias a ubicaciones en ms-logistica
            solicitud.getDestinoId(),           // referencias a ubicaciones en ms-logistica
            request.depositosIntermedios()      // depósitos intermedios elegidos por el operador
        );
        log.info("Creando ruta request: {}", rutaRequest);

        // 3. Llamar a MS-Logística para crear la ruta
        log.info("=== LLAMANDO A logisticaServiceClient ===");
        RutaAsignadaResponseDTO rutaAsignada = logisticaServiceClient.crearRutaParaSolicitud(rutaRequest);
        log.info("RutaAsignadaResponseDTO: {}", rutaAsignada);

        // 4. Obtener estado
        EstadoSolicitud estadoProgramada = estadoSolicitudService.findByNombre("PROGRAMADA");
        
        // 5. Actualizar solicitud con la ruta asignada
        solicitud.setEstado(estadoProgramada);
        solicitud.setDistanciaTotalKM(rutaAsignada.distanciaTotalKM());
        solicitud.setTiempoEstimadoHoras(rutaAsignada.duracionTotalHoras());
        this.save(solicitud);
        
        // 6. Retornar el SolicitudWithRutaResponseDTO que se construye a partir de las entidades solicitud, ruta
        return SolicitudWithRutaResponseDTO.fromEntity(solicitud, rutaAsignada, cliente);
    }


    /**
     * Calcula los costos estimados de una solicitud consultando a MS-Logística
     * y actualiza la solicitud con esos valores.
     *
     * @param solicitudId ID de la solicitud.
     * @return Un CostosEstimadosDTO con los valores calculados.
     * @throws EntidadNotFoundException si la solicitud no existe.
     */
    public CostosEstimadosDTO calcularCostosEstimados(Long solicitudId) {
        log.info("Calculando costos estimados para solicitud ID: {}", solicitudId);
        
        // 1. Buscar solicitud localmente
        Solicitud solicitud = this.findById(solicitudId);

        // 2. Obtener costos desde MS-LOGISTICA
        CostosEstimadosDTO costos = logisticaServiceClient.calcularCostosEstimados(solicitudId);
        
        // 3. Actualizar valores estimados
        solicitud.setCostoEstimado(costos.getCostoEstimado());
        // solicitud.setTiempoEstimadoHoras(costos.getTiempoEstimadoSegundos() / 3600.0);
        this.save(solicitud);

        log.info("Costos estimados calculados exitosamente para solicitud ID: {}", solicitudId);
        return costos;
    }


    /*
    * Calcular costos estimados y asignarlos a la solicitud
    */
    @SuppressWarnings("null")
    public CostoFinalDTO calcularCostosTotales(Long solicitudId) {
        log.info("Calculando costos estimados para solicitud ID: {}", solicitudId);
        
        CostoFinalDTO costos = logisticaServiceClient.calcularCostosTotales(solicitudId);
        
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new EntidadNotFoundException("Solicitud", solicitudId));
        
        solicitud.setCostoFinal(costos.getCostoTotal());
        solicitud.setTiempoReal(costos.getTiempoTotalSegundos()/3600.0);
        this.save(solicitud);

        log.info("Costos estimados calculados exitosamente para solicitud ID: {}", solicitudId);
        return costos;
    }


    /**
     * Consulta el estado actual de la solicitud junto con su ubicación de origen,
     * destino, ruta y tramos asociados.
     *
     * @param solicitudId ID de la solicitud.
     * @return Un DTO con la solicitud, sus ubicaciones y la ruta completa.
     * @throws AccessoDenegadoException si la solicitud no pertenece al cliente autenticado.
     * @throws EntidadNotFoundException si la solicitud no existe.
     */
    public SolicitudWithUbicacionAndRutaResponseDTO seguimientoSolicitud(Long solicitudId) {
        // Por el momento se hizo asi para no dividir en dos endpoints la accion de consulta

        // 1. Validar que el cliente solo pueda ver SUS propias solicitudes
        // String clienteId = securityContextService.obtenerClienteIdDesdeToken();

        // Solicitud solicitud = this.findByIdAndClienteId(solicitudId, clienteId);
        Solicitud solicitud = this.findById(solicitudId);
        
        // 2. Obtener ubicaciones desde MS-LOGISTICA
        UbicacionResponseDTO origen = logisticaServiceClient.obtenerUbicacionPorId(solicitud.getOrigenId());
        UbicacionResponseDTO destino = logisticaServiceClient.obtenerUbicacionPorId(solicitud.getDestinoId());

        // 3. Llamar a MS-LOGISTICA para obtener la ruta y tramos
        RutaTramosCamionResponse ruta = logisticaServiceClient.obtenerRutaPorSolicitudId(solicitudId);
        
        return SolicitudWithUbicacionAndRutaResponseDTO.fromEntity(
            solicitud, origen, destino, ruta 
        );
    }


    /**
     * Obtiene el contenedor asociado a una solicitud.
     *
     * @param solicitudId  ID de la solicitud.
     * @return ContenedorResponseDTO con los datos del contenedor.
     * @throws EntidadNotFoundException si no se encuentra la solicitud.
     */
    @SuppressWarnings("null")
    public ContenedorResponseDTO obtenerContenedor(Long solicitudId) {

        Solicitud solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new EntidadNotFoundException("Solicitud", solicitudId));
        
        Contenedor contenedor = solicitud.getContenedor();
        return ContenedorResponseDTO.fromEntity(contenedor); 
    }


    /**
     * Obtiene un DTO de solicitud por su ID.
     *
     * @param id ID de la solicitud.
     * @return Un SolicitudResponseDTO con los datos de la solicitud.
     * @throws EntidadNotFoundException si no se encuentra la solicitud.
     */
    @SuppressWarnings("null")
    public SolicitudResponseDTO getDTOById(Long id) {

        // 1. Buscar la solicitud por su ID en la base de datos
        // Si no se encuentra, lanza una excepción personalizada
        Solicitud solicitud = solicitudRepository.findById(id)
            .orElseThrow(() -> new EntidadNotFoundException(
                "Solicitud no encontrada con ID: ", id
            ));

        // 2. Obtener el estado asociado a la solicitud, esta en eager no dispara query extra
        var estado = solicitud.getEstado();

        // 3. Consultar al microservicio de clientes usando el clienteId
        // Esto trae la información del cliente real (nombre, email, teléfono, etc.)
        ClienteDTO cliente = clientesServiceClient.obtenerClientePorId(solicitud.getClienteId());

        // 4 Transformar la entidad y sus relaciones en un DTO listo para la respuesta
        return SolicitudResponseDTO.fromEntity(solicitud, estado, cliente);
    }



    /** 
     * Busca una solicitud por su ID y el ID del cliente (Keycloack Id).
     * Solo devuelve la solicitud si pertenece al cliente indicado.
     *
     * @param solicitudId ID de la solicitud.
     * @param clienteId   ID del cliente que debe coincidir.
     * @return La solicitud encontrada.
     * @throws AccessoDenegadoException si el cliente no tiene acceso.
     */
    public Solicitud findByIdAndClienteId(Long solicitudId, String clienteId) {

        return solicitudRepository.findByIdAndClienteId(solicitudId, clienteId)
            .orElseThrow(() -> new AccessoDenegadoException("No tiene acceso a esta solicitud"));
    }


    /**
     * Busca una solicitud por su ID.
     *
     * @param id ID de la solicitud.
     * @return La solicitud encontrada.
     * @throws EntidadNotFoundException si no existe la solicitud.
     */
    @SuppressWarnings("null")
    public Solicitud findById(Long id) {

        return solicitudRepository.findById(id)
            .orElseThrow(() -> new EntidadNotFoundException("Solicitud", id));
    }


    /**
     * Guarda una solicitud en la base de datos.
     *
     * @param e Solicitud a guardar.
     * @return La solicitud guardada.
     */
    @SuppressWarnings("null")
    public Solicitud save(Solicitud e) {
        return this.solicitudRepository.save(e);
    }


    /**
     * Obtiene todas las solicitudes, con opción de filtrar por estado.
     *
     * @param estado Nombre del estado para filtrar (opcional).
     * @return Lista de SolicitudResponseDTO.
     */
    public List<SolicitudResponseDTO> findAll(String estado) {

        // 1 Obtener la lista de solicitudes según el filtro
        List<Solicitud> solicitudes;
        if (estado != null && !estado.trim().isEmpty()) {
            solicitudes = solicitudRepository.findByEstadoNombre(estado);
        } else {
            solicitudes = solicitudRepository.findAll();
        }

        // 2 Mapear a DTOs usando Stream
        return solicitudes.stream()
                .map(solicitud -> {
                    // Obtener estado
                    EstadoSolicitud estadoSolicitud = solicitud.getEstado();
                    
                    // Obtener info del cliente desde microservicio
                    ClienteDTO cliente = clientesServiceClient.obtenerClientePorId(solicitud.getClienteId());
                    
                    // Crear DTO
                    return SolicitudResponseDTO.fromEntity(solicitud, estadoSolicitud, cliente);
                })
                .collect(Collectors.toList());
    }
}
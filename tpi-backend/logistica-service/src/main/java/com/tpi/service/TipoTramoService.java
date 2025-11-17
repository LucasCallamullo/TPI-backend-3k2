package com.tpi.service;

import java.util.List;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.tpi.exception.EntidadNotFoundException;
import com.tpi.repository.TipoTramoRepository;
import com.tpi.model.TipoTramo;

@Service
@RequiredArgsConstructor
public class TipoTramoService {

    private final TipoTramoRepository tipoTramoRepository;
    
    public List<TipoTramo> findAll() {
        return tipoTramoRepository.findAll();
    }
    
    @SuppressWarnings("null")
    public TipoTramo findById(Long id) {
        return tipoTramoRepository.findById(id)
                .orElseThrow(() -> new EntidadNotFoundException("TipoTramo", id));
    }
    
    @SuppressWarnings("null")
    public TipoTramo save(TipoTramo camion) {
        return tipoTramoRepository.save(camion);
    }

    public TipoTramo findByNombre(String nombre) {
        return tipoTramoRepository.findByNombre(nombre)
            .orElseThrow(() -> new EntidadNotFoundException("TipoTramo", nombre));
    }
}

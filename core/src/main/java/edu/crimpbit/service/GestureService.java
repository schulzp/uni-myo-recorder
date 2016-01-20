package edu.crimpbit.service;

import edu.crimpbit.Gesture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Gesture service.
 */
@Service
public class GestureService {

    @Value("#{'${edu.crimpbit.gestures}'.split(',')}")
    private List<Gesture> gestures;

    public List<Gesture> findAll() {
        return gestures;
    }

}

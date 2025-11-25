package cse.hotel.common.model;

import java.io.Serializable;

public enum RoomStatus implements Serializable { 
    AVAILABLE, 
    OCCUPIED, 
    CLEANING, 
    RESERVED; 
}
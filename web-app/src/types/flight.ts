export interface Flight {
    id: number | null;
    number: string;
    fromCity: string;
    toCity: string;
    departure: string; // ISO string
    arrival: string; // ISO string
    passengerCount: number;
    ticketPrice: number;
    createdAt?: string;
    updatedAt?: string;
}

export interface FlightCreate {
    number: string;
    fromCity: string;
    toCity: string;
    departure: string;
    arrival: string;
    passengerCount: number;
    ticketPrice: number;
}

export interface FlightUpdate {
    id: number;
    number: string;
    fromCity: string;
    toCity: string;
    departure: string;
    arrival: string;
    ticketPrice: number;
}

export interface FlightQuery {
    number?: string;
    fromCity?: string;
    toCity?: string;
    departureFrom?: string;
    departureTo?: string;
}

export interface FlightDelete {
    id: number;
}
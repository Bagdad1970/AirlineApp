export type BookingStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';

export interface Booking {
    id: number | null;
    flightId: number;
    passengerCount: number;
    status: BookingStatus;
    createdAt?: string;
    updatedAt?: string;
}

export interface BookingCreate {
    flightId: number;
    passengerCount: number;
}

export interface BookingUpdate {
    id: number;
    passengerCount: number;
}

export interface BookingDelete {
    id: number;
}

export interface BookingQuery {
    id?: number;
    flightId?: number;
    passengerCount?: number;
}

export interface BookingStatistics {
    totalBookings: number;
    pendingBookings: number;
    confirmedBookings: number;
    cancelledBookings: number;
    completedBookings: number;
    totalPassengers: number;
}
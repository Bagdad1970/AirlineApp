CREATE TABLE IF NOT EXISTS flights (
    id BIGSERIAL PRIMARY KEY,
    number VARCHAR(10) NOT NULL,
    from_city VARCHAR(255) NOT NULL,
    to_city VARCHAR(255) NOT NULL,
    departure TIMESTAMP WITH TIME ZONE NOT NULL,
    arrival TIMESTAMP WITH TIME ZONE NOT NULL,
    passenger_count SMALLINT NOT NULL,
    ticket_price NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_flight_number ON flights(number);
CREATE INDEX IF NOT EXISTS idx_flight_from_city ON flights(from_city);
CREATE INDEX IF NOT EXISTS idx_flight_to_city ON flights(to_city);
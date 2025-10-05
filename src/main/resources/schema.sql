-- Schema for reactive R2DBC application

DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS time_slots;

CREATE TABLE time_slots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    delivery_mode VARCHAR(50) NOT NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    capacity INTEGER NOT NULL,
    current_reservations INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT chk_capacity CHECK (capacity > 0),
    CONSTRAINT chk_reservations CHECK (current_reservations >= 0 AND current_reservations <= capacity)
);

CREATE TABLE reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    time_slot_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    cancelled_at TIMESTAMP,
    FOREIGN KEY (time_slot_id) REFERENCES time_slots(id)
);

CREATE INDEX idx_time_slots_delivery_mode ON time_slots(delivery_mode);
CREATE INDEX idx_time_slots_date ON time_slots(date);
CREATE INDEX idx_time_slots_mode_date ON time_slots(delivery_mode, date);
CREATE INDEX idx_reservations_email ON reservations(customer_email);
CREATE INDEX idx_reservations_status ON reservations(status);
CREATE INDEX idx_reservations_time_slot ON reservations(time_slot_id);

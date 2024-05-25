CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS Crew (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID_GENERATE_V4()),
    name VARCHAR(20) NOT NULL,
    surname VARCHAR(20) NOT NULL,
    position VARCHAR(20) NOT NULL,
    price VARCHAR(20) NOT NULL,
    review VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS Company (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID_GENERATE_V4()),
    name VARCHAR(20) NOT NULL,
    location VARCHAR(20) NOT NULL,
    email VARCHAR(40) NOT NULL,
    password VARCHAR(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS Users (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID_GENERATE_V4()),
    name VARCHAR(20) NOT NULL,
    surname VARCHAR(20) NOT NULL,
    license VARCHAR(20) NOT NULL,
    email VARCHAR(40) NOT NULL,
    password VARCHAR(40) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS Payment (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID_GENERATE_V4()),
    oib VARCHAR(40) NOT NULL,
    iban VARCHAR(40) NOT NULL,
    user_id CHAR(36) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(id)
);

CREATE TABLE IF NOT EXISTS Boats (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID_GENERATE_V4()),
    company_id CHAR(36) NOT NULL,
    img VARCHAR(100),
    price VARCHAR(20) NOT NULL,
    availability VARCHAR(20) NOT NULL,
    review VARCHAR(20),
    FOREIGN KEY (company_id) REFERENCES Company(id)
);

CREATE TABLE IF NOT EXISTS Reservation (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID_GENERATE_V4()),
    user_id CHAR(36) NOT NULL,
    boat_id CHAR(36) NOT NULL,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    down_payment VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(id),
    FOREIGN KEY (boat_id) REFERENCES Boats(id)
);

CREATE TABLE IF NOT EXISTS BoatType (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID_GENERATE_V4()),
    name VARCHAR(20) NOT NULL,
    type VARCHAR(20) NOT NULL,
    passenger_capacity INT,
    length VARCHAR(20) NOT NULL,
    width VARCHAR(20) NOT NULL,
    height VARCHAR(20) NOT NULL,
    crew_capacity INT
);

CREATE TABLE IF NOT EXISTS CrewCompany (
    crew_id CHAR(36) NOT NULL,
    company_id CHAR(36) NOT NULL,
    PRIMARY KEY (crew_id, company_id),
    FOREIGN KEY (crew_id) REFERENCES Crew(id),
    FOREIGN KEY (company_id) REFERENCES Company(id)
);

CREATE TABLE IF NOT EXISTS CrewReservation (
    crew_id CHAR(36) NOT NULL,
    reservation_id CHAR(36) NOT NULL,
    PRIMARY KEY (crew_id, reservation_id),
    FOREIGN KEY (crew_id) REFERENCES Crew(id),
    FOREIGN KEY (reservation_id) REFERENCES Reservation(id)
);
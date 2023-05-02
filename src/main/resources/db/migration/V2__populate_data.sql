INSERT INTO Client(name)
VALUES('Wire');

INSERT INTO Employee(name, client_id)
VALUES('Alice', currval(pg_get_serial_sequence('client','client_id')));

INSERT INTO Dependant(name, employee_id)
VALUES ('Oliver', currval(pg_get_serial_sequence('employee','employee_id')));

INSERT INTO Employee(name, client_id)
VALUES('Bob', currval(pg_get_serial_sequence('client','client_id')));

INSERT INTO Dependant(name, employee_id)
VALUES ('Anna', currval(pg_get_serial_sequence('employee','employee_id')));

INSERT INTO Employee(name, client_id)
VALUES('Carl', currval(pg_get_serial_sequence('client','client_id')));

INSERT INTO Benefit(name, price)
VALUES('Health Care Special', 135.20);

INSERT INTO Benefit(name, price)
VALUES('World Class Gym', 90);

INSERT INTO Benefit(name, price)
VALUES('Spotify', 10);


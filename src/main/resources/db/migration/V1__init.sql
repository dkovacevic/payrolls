create sequence client_id_seq as integer;

create table Client
(
    client_id     serial
        constraint client_pk
            primary key,
    name            varchar not null
);

create table Employee
(
    employee_id     serial
        constraint employee_pk
            primary key,
    name            varchar not null,
    benefit_balance real default 1000,
    gross           real default 2000,
    client_id       serial constraint employee_client_client_id_fk
                                                 references Client
);

create table Dependant
(
    dependant_id serial
        constraint dependant_pk
            primary key,
    name         varchar not null,
    employee_id  serial
        constraint dependant_employee_employee_id_fk
            references Employee
);

create table Benefit
(
    benefit_id serial constraint benefit_pk primary key,
    name       varchar                                                     not null,
    price      real                                                        not null
);

create table Employee_Benefit
(
    id          serial
        constraint employee_benefit_pk_2
            primary key,
    employee_id serial
        constraint employee_benefit_employee_employee_id_fk
            references Employee,
    benefit_id  serial
        constraint employee_benefit_benefit_benefit_id_fk
            references Benefit,
    price       real not null,
    paid        boolean default false,
    dependant   boolean default false
);

-- TODO: add index on (benefit_id,employee_id)

create table Paycheck
(
    paycheck_id   serial
        constraint paycheck_pk
            primary key,
    start_date        date not null,
    end_date          date not null,
    employee_id   serial
        constraint paycheck_employee_employee_id_fk
            references Employee,
    client_id   serial
            constraint paycheck_client_client_id_fk
                references Client,
    benefits_paid real not null,
    net           real not null,
    gross         real not null
);


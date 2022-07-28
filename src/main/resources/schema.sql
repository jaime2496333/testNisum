create table PHONE (
    id bigint auto_increment not null,
    created timestamp not null,
    created_by varchar(100),
    modified timestamp not null,
    modified_by varchar(100),
    city_code varchar(10),
    country_code varchar(10),
    number varchar(10) not null,
    user_id binary not null,
    primary key (id)
);

create table ROLE (
    id bigint auto_increment not null,
    created timestamp not null,
    created_by varchar(100),
    modified timestamp not null,
    modified_by varchar(100),
    description varchar(255),
    name varchar(100),
    primary key (id)
);

create table USER (
    id binary not null,
    created timestamp not null,
    created_by varchar(100),
    modified timestamp not null,
    modified_by varchar(100),
    email varchar(100) not null,
    is_active boolean,
    last_login timestamp,
    name varchar(100),
    password varchar(255) not null,
    token varchar(255),
    primary key (id)
);

create table USER_HAS_ROLE (
    user_id binary not null,
    role_id bigint not null,
    primary key (user_id, role_id)
);

alter table ROLE add constraint ROLE_NAME_UINDEX unique (name);
alter table PHONE add constraint PHONE_NUMBER_UINDEX unique (number);
alter table PHONE add constraint PHONE_USER__FK foreign key (user_id) references USER;
alter table USER_HAS_ROLE add constraint USER_HAS_ROLE_ROLE__FK foreign key (role_id) references ROLE;
alter table USER_HAS_ROLE add constraint USER_HAS_ROLE_USER__FK foreign key (user_id) references USER;
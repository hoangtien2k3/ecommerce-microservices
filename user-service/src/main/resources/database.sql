
-- Database: userservice

CREATE DATABASE userservice;
USE userservice;

-- table roles
create table roles (
    id bigint not null auto_increment,
    name varchar(60),
    primary key (id)
) engine=MyISAM;

-- table use_role
create table use_role (
       user_id bigint not null,
       role_id bigint not null,
       primary key (user_id, role_id)
) engine=MyISAM;

-- table users
create table users (
       id bigint not null auto_increment,
       avatar longtext,
       email varchar(255),
       name varchar(255),
       password varchar(255),
       username varchar(255),
       primary key (id)
) engine=MyISAM;

-- table hibernate_sequence
create table hibernate_sequence (
       next_val bigint
) engine=MyISAM;


insert into userservice.roles (id, name)
values	('1', 'USER'),
		('2', 'PM'),
		('3', 'ADMIN');

alter table roles
       add constraint UK_nb4h0p6txrmfc0xbrd1kglp9t unique (name);

alter table users
       add constraint UKr43af9ap4edm43mmtq01oddj6 unique (username);

alter table users
       add constraint UK6dotkott2kjsp8vw4d0m25fb7 unique (email);

alter table user_role
       add constraint FKt7e7djp752sqn6w22i6ocqy6q
       foreign key (role_id)
       references roles (id);

alter table user_role
       add constraint FKj345gk1bovqvfame88rcx7yyx
       foreign key (user_id)
       references users (id);

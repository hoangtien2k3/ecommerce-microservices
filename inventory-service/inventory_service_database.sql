-- table inventory
create table inventory (
       id bigint not null auto_increment,
       quantity integer,
       sku_code varchar(255),
       primary key (id)
) engine=MyISAM
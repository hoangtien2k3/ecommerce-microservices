-- table inventory
create table inventory (
       id bigint not null auto_increment,
       productName varchar(255) not null,
       quantity int not null,
       primary key (id)
) engine=MyISAM
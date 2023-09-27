
-- Order-Service database:

-- table orders
create table orders (
       id bigint not null auto_increment,
        order_number varchar(255),
        primary key (id)
) engine=MyISAM


-- table order_line_items
create table order_line_items (
       id bigint not null auto_increment,
        order_number varchar(255),
        price decimal(19,2),
        quantity integer,
        sku_code varchar(255),
        primary key (id)
) engine=MyISAM


-- table orders_order_line_items_list
create table orders_order_line_items_list (
       order_id bigint not null,
        order_line_items_list_id bigint not null
) engine=MyISAM


alter table orders_order_line_items_list
       add constraint UK_ao6a1tas0iyb7iju31c5b7ef8 unique (order_line_items_list_id)


alter table orders_order_line_items_list
       add constraint FK9itkpvs1xr2gte662cyk3u736
       foreign key (order_line_items_list_id)
       references order_line_items (id)


alter table orders_order_line_items_list
       add constraint FK7o4imo7495iqco6yaacgnm2c4
       foreign key (order_id)
       references orders (id)
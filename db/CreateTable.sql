create table "TRAVALE".USERS (
        id bigserial not null,
        PASSWORD varchar(256) not null,
        PASSWORD_RESET_TOKEN varchar(256),
        ROLES varchar(256) not null,
        USER_NAME varchar(256) not null,
        primary key (id)
    );
	
create table "TRAVALE".USER_INFO (
		id  bigserial not null,
		FIRST_NAME varchar(256),
		LAST_NAME varchar(256),
		PICTURE varchar(256),
		USER_ID int8 not null,
		primary key (id)
    );

alter table "TRAVALE".USERS 
		add constraint UK_21q8fvry4wix31petp1awxsx9 unique (USER_NAME);
    
alter table "TRAVALE".USER_INFO 
       add constraint FKasljti19krenaxiv3o8047af6 
       foreign key (USER_ID) 
       references "TRAVALE".USERS;
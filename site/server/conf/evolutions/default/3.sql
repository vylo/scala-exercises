# --- !Ups

CREATE TABLE userProgress (
    id integer primary key auto_increment,
    userId integer not null,
    libraryName varchar(255) not null,
    sectionName varchar(255) not null,
    method varchar(255) not null,
    args varchar(255),
    succeeded boolean,
FOREIGN KEY fk_users_userid (userId) REFERENCES users (id));

# --- !Downs

DROP TABLE userProgress;

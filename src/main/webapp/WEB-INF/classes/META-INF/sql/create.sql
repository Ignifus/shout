create table users(id INT NOT NULL AUTO_INCREMENT, email varchar(256) not null,hash varchar(256) not null,salt varchar(256) not null,isauthenticated boolean not null,PRIMARY KEY (id));

create table shouts(id int not null AUTO_INCREMENT,user_id int not null,content varchar(256) not null,date DATETIME not null,PRIMARY KEY (id),FOREIGN KEY (user_id) REFERENCES users(id));

create table comments(id int not null AUTO_INCREMENT,user_id int not null, shout_id int not null, content varchar(256) not null, date DATETIME not null, PRIMARY KEY(id), FOREIGN KEY (user_id) REFERENCES users(id), FOREIGN KEY (shout_id) REFERENCES shouts(id));

INSERT INTO users values(1,'shout@shout.com', 'dtb/tiAi8P0a/NGDh4LW/w==', 'CmZ8PNZlVm28PLsTKgR8pg==', false);
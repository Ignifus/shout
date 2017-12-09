create table users(id INT NOT NULL AUTO_INCREMENT, email varchar(256) not null,hash varchar(256) not null,salt varchar(256) not null,wskey varchar(256) not null,avatar varchar(256) null,PRIMARY KEY (id));

create table follows(follower_user_id int not null,followed_user_id int not null,PRIMARY KEY (follower_user_id, followed_user_id),FOREIGN KEY (follower_user_id) REFERENCES users(id), FOREIGN KEY (followed_user_id) REFERENCES users(id));

create table shouts(id int not null AUTO_INCREMENT,user_id int not null,content varchar(256) not null, image TEXT null, date DATETIME not null,PRIMARY KEY (id),FOREIGN KEY (user_id) REFERENCES users(id));

create table upvotes(user_id int not null,shout_id int not null,PRIMARY KEY (user_id, shout_id),FOREIGN KEY (user_id) REFERENCES users(id), FOREIGN KEY (shout_id) REFERENCES shouts(id));

create table comments(id int not null AUTO_INCREMENT,user_id int not null, shout_id int not null, content varchar(256) not null, date DATETIME not null, PRIMARY KEY(id), FOREIGN KEY (user_id) REFERENCES users(id), FOREIGN KEY (shout_id) REFERENCES shouts(id));

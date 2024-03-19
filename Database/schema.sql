BEGIN TRANSACTION;

DROP TABLE IF EXISTS steam_game;

CREATE TABLE steam_game 
(
	game_id 					serial		  NOT NULL,
	name 						text   		  NOT NULL,
	release_date 				text		  NOT NULL,
	required_age 				int			  NOT NULL,
	price                   	decimal(13, 2) ,
	about						text ,
	image_url					text ,
	is_for_windows				boolean , 
	is_for_mac					boolean ,
	is_for_linux 				boolean , 
	categories 					text[] ,
	genres 						text[] ,
	tags 						text[] ,
	CONSTRAINT PK_game_id PRIMARY KEY (game_id)
);

COMMIT TRANSACTION;


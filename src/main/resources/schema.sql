DROP TABLE IF EXISTS film_like CASCADE;
DROP TABLE IF EXISTS genre_of_film CASCADE;
DROP TABLE IF EXISTS friendship CASCADE;
DROP TABLE IF EXISTS review_dislikes CASCADE;
DROP TABLE IF EXISTS review_likes CASCADE;
DROP TABLE IF EXISTS review CASCADE;
DROP TABLE IF EXISTS "user" CASCADE;
DROP TABLE IF EXISTS film CASCADE;

CREATE TABLE IF NOT EXISTS "user"
(
    user_id  integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    varchar,
    login    varchar,
    birthday date,
    name     varchar
);

CREATE TABLE IF NOT EXISTS friendship
(
    user_id       integer,
    friendUser_id integer
);

CREATE TABLE IF NOT EXISTS film
(
    film_id       integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name          varchar,
    description   varchar,
    released_date date,
    duration      int,
    mpa_id        integer
);

CREATE TABLE IF NOT EXISTS film_like
(
    film_id integer,
    user_id integer
);

CREATE TABLE IF NOT EXISTS genre_of_film
(
    film_id  integer,
    genre_id integer
);

CREATE TABLE IF NOT EXISTS genre
(
    genre_id   integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name varchar(50)
);

CREATE TABLE IF NOT EXISTS mpa
(
    mpa_id   integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    mpa_name varchar(50)
);

CREATE TABLE IF NOT EXISTS director_of_film
(
    film_id  integer,
    director_id integer
);

CREATE TABLE IF NOT EXISTS director
(
    director_id   integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    director_name varchar(50)
);

CREATE TABLE IF NOT EXISTS review
(
    review_id   integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content     varchar(255),
    is_positive BOOLEAN,
    user_id     integer,
    film_id     integer,
    useful      integer DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES "user" (user_id),
    FOREIGN KEY (film_id) REFERENCES film (film_id)
);

CREATE TABLE IF NOT EXISTS review_likes
(
    review_id integer,
    user_id   integer,
    FOREIGN KEY (review_id) REFERENCES review (review_id),
    FOREIGN KEY (user_id) REFERENCES "user" (user_id)
);

CREATE TABLE IF NOT EXISTS review_dislikes
(
    review_id integer,
    user_id   integer,
    FOREIGN KEY (review_id) REFERENCES review (review_id),
    FOREIGN KEY (user_id) REFERENCES "user" (user_id)
);


ALTER TABLE friendship
    ADD FOREIGN KEY (user_id) REFERENCES "user" (user_id);

ALTER TABLE friendship
    ADD FOREIGN KEY (friendUser_id) REFERENCES "user" (user_id);

ALTER TABLE film_like
    ADD FOREIGN KEY (film_id) REFERENCES film (film_id);

ALTER TABLE film_like
    ADD FOREIGN KEY (user_id) REFERENCES "user" (user_id);

ALTER TABLE genre_of_film
    ADD FOREIGN KEY (film_id) REFERENCES film (film_id);

ALTER TABLE genre_of_film
    ADD FOREIGN KEY (genre_id) REFERENCES genre (genre_id);

ALTER TABLE film
    ADD FOREIGN KEY (mpa_id) REFERENCES mpa (mpa_id);


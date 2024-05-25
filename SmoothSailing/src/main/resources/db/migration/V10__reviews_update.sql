ALTER TABLE reservation
ADD COLUMN reviewed VARCHAR(20);

ALTER TABLE boats
ADD COLUMN review_sum VARCHAR(20),
ADD COLUMN number_of_reviews VARCHAR(20);
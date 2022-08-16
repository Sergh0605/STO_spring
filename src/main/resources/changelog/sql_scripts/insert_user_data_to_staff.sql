UPDATE STAFF
SET LOGIN    = 'admin',
    PASSWORD = '12345',
    ROLE     = 'ADMIN'
WHERE ID = 1;

UPDATE STAFF
SET LOGIN    = 'master1',
    PASSWORD = '12345',
    ROLE     = 'ACCEPTOR'
WHERE ID = 2;

UPDATE STAFF
SET LOGIN    = 'supervisor',
    PASSWORD = '12345',
    ROLE     = 'ACCEPTOR'
WHERE ID = 3;

UPDATE STAFF
SET LOGIN    = 'director',
    PASSWORD = '12345',
    ROLE     = 'ADMIN'
WHERE ID = 4;

UPDATE STAFF
SET LOGIN    = 'mechanic',
    PASSWORD = '12345',
    ROLE     = 'ACCEPTOR'
WHERE ID = 5;

UPDATE STAFF
SET LOGIN    = 'user',
    PASSWORD = '12345',
    ROLE     = 'USER'
WHERE ID = 6;

UPDATE STAFF
SET LOGIN    = 'master2',
    PASSWORD = '12345',
    ROLE     = 'ACCEPTOR'
WHERE ID = 7;
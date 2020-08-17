SELECT balance FROM accounts
JOIN users ON users.user_id = accounts.user_id
WHERE users.username = 'gamerunner15';

UPDATE accounts
SET balance = (accounts.balance - 50)
WHERE user_id = (SELECT user_id FROM users WHERE username = 'gamerunner15');

INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount)
VALUES (2, 2, (SELECT user_id FROM users WHERE username = 'gamerunner15'), (SELECT user_id FROM users WHERE username = 'admin'), 50);

SELECT transfers.transfer_id, (SELECT username FROM users 
JOIN accounts ON accounts.user_id = users.user_id
JOIN transfers ON transfers.account_from = accounts.account_id
WHERE transfers.account_from = 3 GROUP BY username) AS Sender, transfers.amount, 
(SELECT users.username from users WHERE accounts.account_id = (SELECT account_to FROM transfers WHERE account_from = 3 GROUP BY account_to)) AS Recipient
FROM transfers
JOIN transfer_statuses ON transfer_statuses.transfer_status_id = transfers.transfer_status_id
JOIN transfer_types ON transfer_types.transfer_type_id = transfers.transfer_type_id
JOIN accounts ON accounts.account_id = transfers.account_from
JOIN users ON users.user_id = accounts.user_id
WHERE account_from  = 3 OR account_to = 3;

SELECT transfers.transfer_id,  FROM transfers
JOIN transfer_statuses ON transfer_statuses.transfer_status_id = transfers.transfer_status_id
JOIN transfer_types ON transfer_types.transfer_type_id = transfers.transfer_type_id
JOIN accounts ON accounts.account_id = transfers.account_to
JOIN users ON users.user_id = accounts.user_id
WHERE account_from  = 3 OR account_to = 3;


SELECT username FROM users 
JOIN accounts ON accounts.user_id = users.user_id
JOIN transfers ON transfers.account_from = accounts.account_id
WHERE transfers.account_from = 3 GROUP BY username;


SELECT users.username from users 
JOIN accounts ON accounts.user_id = users.user_id
WHERE accounts.account_id = (SELECT account_to FROM transfers WHERE account_from = 3 GROUP BY account_to ORDER BY account_to LIMIT 1);

\c ibmq;

CREATE TABLE partners (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    alias VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    direction VARCHAR(10) NOT NULL,
    application TEXT,
    processed_flow_type VARCHAR(50),
    description TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

     CONSTRAINT chk_direction CHECK (direction IN ('INBOUND', 'OUTBOUND')),
     CONSTRAINT chk_processed_flow_type CHECK (processed_flow_type IN ('MESSAGE', 'ALERTING', 'NOTIFICATION'))
);

CREATE INDEX idx_alias ON partners (alias);
CREATE INDEX idx_type_direction ON partners (type, direction);


CREATE TABLE messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content TEXT NOT NULL,
    received_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_received_at ON messages (received_at);

-- Mock data into the partners table
INSERT INTO partners (alias, type, direction, application, processed_flow_type, description) VALUES
('Partner1', 'SYSTEM', 'INBOUND', 'App1', 'MESSAGE', 'Description for Partner1'),
('Partner2', 'HUMAN', 'OUTBOUND', 'App2', 'NOTIFICATION', 'Description for Partner2'),
('Partner3', 'SYSTEM', 'INBOUND', 'App3', 'ALERTING', 'Description for Partner3'),
('Partner4', 'HUMAN', 'OUTBOUND', 'App4', 'MESSAGE', 'Description for Partner4'),
('Partner5', 'SYSTEM', 'INBOUND', 'App5', 'ALERTING', 'Description for Partner5'),
('Partner6', 'SYSTEM', 'OUTBOUND', 'App6', 'NOTIFICATION', 'Description for Partner6'),
('Partner7', 'HUMAN', 'INBOUND', 'App7', 'MESSAGE', 'Description for Partner7'),
('Partner8', 'SYSTEM', 'OUTBOUND', 'App8', 'NOTIFICATION', 'Description for Partner8'),
('Partner9', 'HUMAN', 'INBOUND', 'App9', 'ALERTING', 'Description for Partner9'),
('Partner10', 'SYSTEM', 'OUTBOUND', 'App10', 'MESSAGE', 'Description for Partner10');

-- Mock data into the messages table
INSERT INTO messages (content, received_at) VALUES
('Message content from Partner1', '2024-11-27 10:00:00'),
('Message content from Partner2', '2024-11-27 10:05:00'),
('Message content from Partner3', '2024-11-27 10:10:00'),
('Message content from Partner4', '2024-11-27 10:15:00'),
('Message content from Partner5', '2024-11-27 10:20:00'),
('Message content from Partner6', '2024-11-27 10:25:00'),
('Message content from Partner7', '2024-11-27 10:30:00'),
('Message content from Partner8', '2024-11-27 10:35:00'),
('Message content from Partner9', '2024-11-27 10:40:00'),
('Message content from Partner10', '2024-11-27 10:45:00');
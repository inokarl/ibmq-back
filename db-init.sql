CREATE DATABASE ibmq;

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
    partner_id UUID NOT NULL,
    content TEXT NOT NULL,
    received_at TIMESTAMP NOT NULL,                      -

    CONSTRAINT fk_partner FOREIGN KEY (partner_id) REFERENCES partners(id) ON DELETE CASCADE
);

CREATE INDEX idx_partner_id ON messages (partner_id);
CREATE INDEX idx_received_at ON messages (received_at);

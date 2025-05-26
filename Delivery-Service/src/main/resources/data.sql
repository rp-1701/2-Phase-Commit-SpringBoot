-- Initialize Delivery Agents table with 8 agents in 'Koramangala'
INSERT INTO delivery_agents (agent_id, name, current_location, status, reserved_for_order_id) VALUES
(1, 'Agent 1', 'Koramangala', 'AVAILABLE', null),
(2, 'Agent 2', 'Koramangala', 'AVAILABLE', null),
(3, 'Agent 3', 'Koramangala', 'AVAILABLE', null),
(4, 'Agent 4', 'Koramangala', 'AVAILABLE', null),
(5, 'Agent 5', 'Koramangala', 'AVAILABLE', null),
(6, 'Agent 6', 'Koramangala', 'BUSY', null),      -- Already on delivery
(7, 'Agent 7', 'Koramangala', 'BUSY', null),      -- Already on delivery
(8, 'Agent 8', 'Koramangala', 'BUSY', null);      -- Already on delivery 
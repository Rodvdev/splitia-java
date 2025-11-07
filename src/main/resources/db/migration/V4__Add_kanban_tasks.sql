-- Splitia Database Schema Migration V4
-- Add Kanban tasks and task tags tables

-- Create Task Tags table
CREATE TABLE IF NOT EXISTS task_tags (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(50) NOT NULL,
    color VARCHAR(7),
    group_id UUID NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT unique_tag_per_group UNIQUE (group_id, name)
);

CREATE INDEX IF NOT EXISTS idx_task_tags_group ON task_tags(group_id);
CREATE INDEX IF NOT EXISTS idx_task_tags_deleted_at ON task_tags(deleted_at) WHERE deleted_at IS NULL;

-- Create Tasks table
CREATE TABLE IF NOT EXISTS tasks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'TODO',
    start_date DATE,
    due_date DATE,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    group_id UUID NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    assigned_to_id UUID REFERENCES users(id) ON DELETE SET NULL,
    created_by_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    position INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tasks_group ON tasks(group_id);
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status);
CREATE INDEX IF NOT EXISTS idx_tasks_assigned_to ON tasks(assigned_to_id);
CREATE INDEX IF NOT EXISTS idx_tasks_created_by ON tasks(created_by_id);
CREATE INDEX IF NOT EXISTS idx_tasks_due_date ON tasks(due_date);
CREATE INDEX IF NOT EXISTS idx_tasks_deleted_at ON tasks(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_tasks_group_status_position ON tasks(group_id, status, position);

-- Create junction table for tasks and tags (many-to-many)
CREATE TABLE IF NOT EXISTS task_task_tags (
    task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES task_tags(id) ON DELETE CASCADE,
    PRIMARY KEY (task_id, tag_id)
);

CREATE INDEX IF NOT EXISTS idx_task_task_tags_task ON task_task_tags(task_id);
CREATE INDEX IF NOT EXISTS idx_task_task_tags_tag ON task_task_tags(tag_id);

-- Add triggers for updated_at
CREATE TRIGGER update_plans_updated_at BEFORE UPDATE ON plans FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_tasks_updated_at BEFORE UPDATE ON tasks FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_task_tags_updated_at BEFORE UPDATE ON task_tags FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();


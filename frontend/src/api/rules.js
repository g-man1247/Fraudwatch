import { apiClient } from './client';

export const getRules = () => {
  return apiClient('/api/rules');
};

export const createRule = (ruleData) => {
  return apiClient('/api/rules', {
    method: 'POST',
    body: JSON.stringify(ruleData),
  });
};

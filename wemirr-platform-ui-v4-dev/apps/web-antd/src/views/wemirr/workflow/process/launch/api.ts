import { defHttp } from '#/api/request';

export const GetFormConfigByModelId = (modelId: string) => {
  return defHttp.get(`/workflow/flow-definitions/${modelId}/form-design`);
};

export const startProcessInstance = (modelId: string, data: any) => {
  return defHttp.post(`/workflow/flow-definitions/${modelId}/start`, data);
};

export const ProcessModelGroupList = () => {
  return defHttp.get(`/workflow/flow-definitions/group-list`);
};

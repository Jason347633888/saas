import type { ID } from '#/api/common';

import { defHttp } from '#/api/request';

export function PageList(params: any) {
  return defHttp.get(`/workflow/flow-definitions`, { params });
}

export function AddProcessModel(data: any) {
  return defHttp.request(`/workflow/flow-definitions/create`, {
    method: 'post',
    data,
  });
}

export function getActiveProcessCategory() {
  return defHttp.get(`/workflow/flow-categories/list`, {
    params: { status: 1 },
  });
}

export function ModifyProcessModel(data: any) {
  return defHttp.request(`/workflow/flow-definitions/${data.id}`, {
    method: 'put',
    data,
  });
}

export function GetById(id: ID) {
  return defHttp.get(`/workflow/flow-definitions/${id}/detail`);
}

export function GetInstanceById(id: any) {
  return defHttp.get(`/workflow/flow-instances/${id}/detail`);
}

export function DelObj(id: string, data: any) {
  return defHttp.delete(`/workflow/flow-definitions/${id}`, {
    data,
  });
}

export function Publish(id: any) {
  return defHttp.put(`/workflow/flow-definitions/${id}/publish`);
}
export function UnPublish(id: any) {
  return defHttp.put(`/workflow/flow-definitions/${id}/unpublish`);
}
export function saveFormDesign(id: any, data: any) {
  return defHttp.post(`/workflow/flow-definitions/${id}/form-design`, data);
}
export const getFormByModelId = (defId: string) => {
  return defHttp.get(`/workflow/flow-definitions/${defId}/form-design`);
};

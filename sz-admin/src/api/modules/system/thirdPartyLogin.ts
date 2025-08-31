import http from '@/api';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type { ThirdPartyLoginDTO, ThirdPartyLoginVO } from '@/api/types/system/thirdPartyLogin';

/**
 * 第三方登录
 * @param params
 * @returns {*}
 */
export const thirdPartyLoginApi = (params: ThirdPartyLoginDTO) => {
    return http.post<ThirdPartyLoginVO>(ADMIN_MODULE + `/third-party-login`, params);
};
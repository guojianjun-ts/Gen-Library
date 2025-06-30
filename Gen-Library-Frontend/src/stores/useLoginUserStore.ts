import { ref } from 'vue'
import { defineStore } from 'pinia'
import { getLoginUserUsingGet } from '@/api/userController.ts'

export const useLoginUserStore = defineStore('loginUser', () => {
  /**
   * 存储登录用户信息的状态
   */
  const loginUser = ref<API.LoginUserVO>({
    userName: '未登录',
  })

  /**
   * 远程获取登录用户信息
   */
  async function fetchLoginUser() {
    const res = await getLoginUserUsingGet()
    if (res.data.code === 0 && res.data.data) {
      loginUser.value = res.data.data
    }
  }

  /**
   * 设置登录用户
   * @param newLoginUser
   */
  function setLoginUser(newLoginUser: any) {
    loginUser.value = newLoginUser
  }

  return { loginUser, fetchLoginUser, setLoginUser }
})

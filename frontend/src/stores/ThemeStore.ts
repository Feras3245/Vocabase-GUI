import { defineStore } from 'pinia'
import * as cookie from 'cookie'

export const useThemeStore = defineStore('theme', {
  state: () => {
    const themeCookie = cookie.parse(document.cookie).theme;
    const val = (typeof themeCookie === 'undefined') ? 'light' : themeCookie
    return {theme : val}
  },
  actions: {
    toggle() {
      if (this.theme === 'light')
        this.theme = 'dark'
      else
        this.theme = 'light'
      const nextYearDate = new Date();
      nextYearDate.setFullYear(nextYearDate.getFullYear() + 1);
      document.cookie = cookie.serialize('theme', this.theme, {maxAge:31536000, expires:nextYearDate});
    }
  }
})
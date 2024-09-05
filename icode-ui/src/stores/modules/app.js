import Cookies from 'js-cookie'
import { useDark, useToggle } from '@vueuse/core'
import { constantRoutes } from '@/router/routes'

const state = {
	sidebar: {
		opened: Cookies.get('sidebarStatus') ? !!+Cookies.get('sidebarStatus') : true,
		withoutAnimation: false,
	},
	topbarRouters: [],
	sidebarRouters: [],
	// isDark: useDark(),
	showMenuIndex: 0,
	currentMenuIndex: 0,
}

const actions = {
	toggleSideBar() {
		this.app.sidebar.opened = !this.app.sidebar.opened
		this.app.sidebar.withoutAnimation = false
		if (this.app.sidebar.opened) {
			Cookies.set('sidebarStatus', 1)
		} else {
			Cookies.set('sidebarStatus', 0)
		}
	},
	closeSideBar({ withoutAnimation }) {
		Cookies.set('sidebarStatus', 0)
		this.app.sidebar.opened = false
		this.app.sidebar.withoutAnimation = withoutAnimation
	},
	setBusininessDesc(busininessDesc) {
		this.app.busininessDesc = busininessDesc
	},
}

export default {
	state,
	actions,
}

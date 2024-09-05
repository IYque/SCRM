const getters = {
  sidebar: (state) => state.app.sidebar,
  avatar: (state) => state.user.avatar,
  name: (state) => state.user.name,
  sidebarRouters: (state) => state.app.sidebarRouters,
}
export default getters

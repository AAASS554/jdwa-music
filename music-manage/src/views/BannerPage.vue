<template>
  <div class="container">
    <div class="handle-box">
      <el-button @click="deleteAll">批量删除</el-button>
      <el-input v-model="searchWord" placeholder="筛选关键词"></el-input>
      <el-button type="primary" @click="centerDialogVisible = true">添加轮播图</el-button>
    </div>

    <el-table height="550px" border size="small" :data="data" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="40" align="center"></el-table-column>
      <el-table-column label="ID" prop="id" width="50" align="center"></el-table-column>
      <el-table-column label="图片" width="120" align="center">
        <template v-slot="scope">
          <div class="banner-img">
            <img :src="attachImageUrl(scope.row.pic)" style="width: 100%" />
          </div>
          <el-upload 
            :action="uploadUrl(scope.row.id)" 
            :show-file-list="false" 
            :on-success="handleImgSuccess"
            :before-upload="beforeImgUpload">
            <el-button>更新图片</el-button>
          </el-upload>
        </template>
      </el-table-column>
      <el-table-column label="标题" prop="title" width="120"></el-table-column>
      <el-table-column label="排序" prop="sort" width="120" align="center"></el-table-column>
      <el-table-column label="操作" width="120" align="center">
        <template v-slot="scope">
          <el-button @click="editRow(scope.row)">编辑</el-button>
          <el-button type="danger" @click="deleteRow(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="pagination"
      background
      layout="total, prev, pager, next"
      :current-page="currentPage"
      :page-size="pageSize"
      :total="tableData.length"
      @current-change="handleCurrentChange"
    >
    </el-pagination>
  </div>

  <!-- 添加轮播图弹窗 -->
  <el-dialog v-model="centerDialogVisible" title="添加轮播图" width="400px" center>
    <el-form :model="registerForm" label-width="80px">
      <el-form-item label="标题">
        <el-input v-model="registerForm.title"></el-input>
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="registerForm.sort" :min="1"></el-input-number>
      </el-form-item>
      <el-form-item label="图片">
        <el-upload
          class="upload-demo"
          :action="uploadUrl()"
          :show-file-list="false"
          :on-success="handleImgUpload"
          :before-upload="beforeImgUpload">
          <el-button type="primary">选择图片</el-button>
        </el-upload>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="centerDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addBanner">确定</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 编辑弹窗 -->
  <el-dialog v-model="editVisible" title="编辑" width="400px" center>
    <el-form :model="editForm" label-width="80px">
      <el-form-item label="标题">
        <el-input v-model="editForm.title"></el-input>
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="editForm.sort" :min="1"></el-input-number>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">确定</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 删除提示框 -->
  <jdwa-del-dialog :delVisible="delVisible" @confirm="confirm" @cancelRow="delVisible = $event"></jdwa-del-dialog>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, watch, ref, reactive } from "vue";
import { HttpManager } from "@/api/index";
import JdwaDelDialog from "@/components/dialog/JdwaDelDialog.vue";
import mixin from "@/mixins/mixin";

export default defineComponent({
  components: {
    JdwaDelDialog,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const { beforeImgUpload } = mixin();

    const tableData = ref([]); // 记录轮播图数据
    const tempDate = ref([]); // 记录轮播图，用于搜索时能临时记录一份数据
    const pageSize = ref(10); // 页数
    const currentPage = ref(1); // 当前页

    // 计算当前表格中的数据
    const data = ref([]);
    watch([tableData, currentPage], () => {
      data.value = tableData.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
    });

    const searchWord = ref(""); // 记录输入框输入的内容
    watch(searchWord, () => {
      if (searchWord.value === "") {
        tableData.value = tempDate.value;
      } else {
        tableData.value = [];
        for (let item of tempDate.value) {
          if (item.title.includes(searchWord.value)) {
            tableData.value.push(item);
          }
        }
      }
    });

    getData();

    // 获取轮播图数据
    async function getData() {
      tableData.value = [];
      tempDate.value = [];
      const result = (await HttpManager.getAllBanner()) as ResponseBody;
      tableData.value = result.data;
      tempDate.value = result.data;
      currentPage.value = 1;
    }

    // 添加轮播图
    const registerForm = reactive({
      title: "",
      pic: "",
      sort: 1
    });

    const centerDialogVisible = ref(false);
    async function addBanner() {
      let params = new FormData();
      params.append("title", registerForm.title);
      params.append("pic", registerForm.pic);
      params.append("sort", registerForm.sort.toString());

      const result = (await HttpManager.setBanner(params)) as ResponseBody;
      (proxy as any).$message({
        message: result.message,
        type: result.type,
      });

      if (result.success) {
        getData();
        registerForm.title = "";
        registerForm.pic = "";
        registerForm.sort = 1;
      }
      centerDialogVisible.value = false;
    }

    // 编辑
    const editVisible = ref(false);
    const editForm = reactive({
      id: "",
      title: "",
      sort: 1
    });

    function editRow(row) {
      editVisible.value = true;
      editForm.id = row.id;
      editForm.title = row.title;
      editForm.sort = row.sort;
    }

    async function saveEdit() {
      let params = new FormData();
      params.append("id", editForm.id);
      params.append("title", editForm.title);
      params.append("sort", editForm.sort.toString());

      const result = (await HttpManager.updateBanner(params)) as ResponseBody;
      (proxy as any).$message({
        message: result.message,
        type: result.type,
      });

      if (result.success) {
        getData();
      }
      editVisible.value = false;
    }

    // 删除
    const idx = ref(-1);
    const multipleSelection = ref([]);
    const delVisible = ref(false);
    const handleSelectionChange = (val) => {
      multipleSelection.value = val;
    };

    function deleteRow(id) {
      idx.value = id;
      delVisible.value = true;
    }

    async function confirm() {
      const result = (await HttpManager.deleteBanner(idx.value)) as ResponseBody;
      (proxy as any).$message({
        message: result.message,
        type: result.type,
      });

      if (result.success) getData();
      delVisible.value = false;
    }

    // 批量删除
    async function deleteAll() {
      const idArray = [];
      for (let item of multipleSelection.value) {
        idArray.push(item.id);
      }
      const result = (await HttpManager.deleteBanner(idArray.toString())) as ResponseBody;
      (proxy as any).$message({
        message: result.message,
        type: result.type,
      });

      if (result.success) getData();
    }

    // 更新图片
    function uploadUrl(id = "") {
      return HttpManager.uploadBannerUrl(id);
    }

    function handleImgSuccess(res, file) {
      (proxy as any).$message({
        message: res.message,
        type: res.type,
      });
      if (res.success) getData();
    }

    function handleImgUpload(res, file) {
      if (res.success) {
        registerForm.pic = res.data;
      }
    }

    function handleCurrentChange(val) {
      currentPage.value = val;
    }

    return {
      tableData,
      tempDate,
      pageSize,
      currentPage,
      searchWord,
      data,
      centerDialogVisible,
      registerForm,
      editVisible,
      editForm,
      delVisible,
      multipleSelection,
      uploadUrl,
      handleSelectionChange,
      handleCurrentChange,
      handleImgSuccess,
      handleImgUpload,
      beforeImgUpload,
      deleteRow,
      confirm,
      deleteAll,
      addBanner,
      editRow,
      saveEdit,
      attachImageUrl: HttpManager.attachImageUrl,
    };
  },
});
</script>

<style scoped>
.banner-img {
  width: 200px;
  height: 100px;
  border-radius: 5px;
  margin-bottom: 5px;
  overflow: hidden;
}
.banner-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.handle-box {
  margin-bottom: 20px;
}
.handle-box .el-button {
  margin-right: 10px;
}
.pagination {
  margin: 20px 0;
  text-align: center;
}
</style> 